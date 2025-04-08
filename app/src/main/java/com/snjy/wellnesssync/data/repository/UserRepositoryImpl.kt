package com.snjy.wellnesssync.data.repository

import android.util.Log
import com.snjy.wellnesssync.data.local.dao.UserDao
import com.snjy.wellnesssync.data.local.entity.toEntity
import com.snjy.wellnesssync.data.local.entity.toDomainModel
import com.snjy.wellnesssync.data.preferences.UserPreferences
import com.snjy.wellnesssync.data.remote.datasource.UserRemoteDataSource
import com.snjy.wellnesssync.data.remote.dto.UserDto
import com.snjy.wellnesssync.data.remote.dto.toDomainModel
import com.snjy.wellnesssync.domain.model.DietaryPreference
import com.snjy.wellnesssync.domain.model.User
import com.snjy.wellnesssync.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userPreferences: UserPreferences
) : UserRepository {

    private val TAG = "UserRepositoryImpl"

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        age: Int,
        height: Double,
        weight: Double,
        profession: String,
        dietaryPreference: String
    ): Result<User> {
        try {
            Log.d(TAG, "Starting registration process for: $email")

            // Step 1: Register the user with Supabase Authentication
            val authRequest = mapOf(
                "email" to email,
                "password" to password
            )

            val authResponse = userRemoteDataSource.registerUser(authRequest)

            return authResponse.fold(
                onSuccess = { authData ->
                    Log.d(TAG, "Auth registration successful")

                    // Extract the user ID and token from auth response
                    val userId = (authData["user"] as? Map<*, *>)?.get("id")?.toString() ?: UUID.randomUUID().toString()
                    val token = authData["access_token"]?.toString() ?: ""

                    Log.d(TAG, "User ID from auth: $userId")

                    if (token.isBlank()) {
                        Log.e(TAG, "No token received after registration")
                        return Result.failure(Exception("Authentication failed: No token received"))
                    }

                    // Step 2: Create a user profile in the database
                    val userDto = UserDto(
                        id = userId,
                        name = name,
                        email = email,
                        age = age,
                        height = height,
                        weight = weight,
                        profession = profession,
                        dietaryPreference = dietaryPreference,
                        profileImageUrl = null
                    )

                    // Create user profile in Supabase database
                    val userProfileResult = userRemoteDataSource.createUserProfile("Bearer $token", userDto)

                    userProfileResult.fold(
                        onSuccess = { profileData ->
                            Log.d(TAG, "User profile created successfully")

                            // Create domain model and save to local DB
                            val user = profileData.toDomainModel()
                            userDao.insertUser(user.toEntity())

                            // Save user session
                            userPreferences.saveUserSession(userId, token)

                            return Result.success(user)
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Failed to create user profile", error)
                            return Result.failure(error)
                        }
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "Auth registration failed", error)
                    return Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during registration process", e)
            return Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<User> {
        return userRemoteDataSource.loginUser(email, password).map { token ->
            Log.d(TAG, "Login successful, token received")

            // Get or create user in local DB
            val localUser = userDao.getUserByEmail(email)

            val user = if (localUser != null) {
                Log.d(TAG, "Found user in local database")
                localUser.toDomainModel()
            } else {
                Log.d(TAG, "User not found in local database, fetching from remote")
                // Fetch user details from remote
                // We'll try to get the user ID from the JWT token
                val userId = extractUserIdFromToken(token)

                if (userId != null) {
                    val result = userRemoteDataSource.getUserProfile(token, userId)

                    result.fold(
                        onSuccess = { userDtoList ->
                            if (userDtoList.isNotEmpty()) {
                                val userDto = userDtoList.first()
                                val domainUser = userDto.toDomainModel()
                                userDao.insertUser(domainUser.toEntity())
                                domainUser
                            } else {
                                createPlaceholderUser(email)
                            }
                        },
                        onFailure = {
                            Log.e(TAG, "Failed to get user profile after login", it)
                            createPlaceholderUser(email)
                        }
                    )
                } else {
                    createPlaceholderUser(email)
                }
            }

            // Save user session
            userPreferences.saveUserSession(user.id, token)

            user
        }
    }

    private fun extractUserIdFromToken(token: String): String? {
        // A very simple JWT token parsing to extract user ID
        // In a real app, use a proper JWT library
        try {
            val parts = token.split(".")
            if (parts.size >= 2) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                val payloadJson = org.json.JSONObject(payload)
                return payloadJson.optString("sub")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract user ID from token", e)
        }
        return null
    }

    private suspend fun createPlaceholderUser(email: String): User {
        val placeholderUser = User(
            id = UUID.randomUUID().toString(),
            name = "User",
            email = email,
            age = 0,
            height = 0.0,
            weight = 0.0,
            profession = "",
            dietaryPreference = DietaryPreference.VEGETARIAN
        )
        userDao.insertUser(placeholderUser.toEntity())
        return placeholderUser
    }

    override suspend fun logoutUser(): Result<Unit> {
        val token = userPreferences.authToken.first() ?: return Result.failure(Exception("User not logged in"))
        return userRemoteDataSource.logoutUser(token).also {
            // Clear local session regardless of remote logout result
            userPreferences.clearUserSession()
        }
    }

    override suspend fun getUserProfile(): Result<User> {
        val userId = userPreferences.userId.first() ?: return Result.failure(Exception("User not logged in"))
        val token = userPreferences.authToken.first() ?: return Result.failure(Exception("User not logged in"))

        // First try to get from local DB
        val localUser = userDao.getUserById(userId)
        if (localUser != null) {
            return Result.success(localUser.toDomainModel())
        }

        // Fall back to remote
        return userRemoteDataSource.getUserProfile(token, userId).map { userDtoList ->
            if (userDtoList.isNotEmpty()) {
                val userDto = userDtoList.first()
                val user = userDto.toDomainModel()
                userDao.insertUser(user.toEntity())
                user
            } else {
                throw Exception("User not found")
            }
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        val token = userPreferences.authToken.first() ?: return Result.failure(Exception("User not logged in"))

        val userDto = UserDto(
            id = user.id,
            name = user.name,
            email = user.email,
            age = user.age,
            height = user.height,
            weight = user.weight,
            profession = user.profession,
            dietaryPreference = user.dietaryPreference.name,
            profileImageUrl = user.profileImageUrl
        )

        return userRemoteDataSource.updateUserProfile(token, userDto, user.id).map { remoteUserDto ->
            val updatedUser = remoteUserDto.toDomainModel()
            userDao.updateUser(updatedUser.toEntity())
            updatedUser
        }
    }

    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return userPreferences.isLoggedIn
    }

    override suspend fun getUserThemePreference(): Flow<Boolean> {
        return userPreferences.darkMode
    }

    override suspend fun setUserThemePreference(isDarkMode: Boolean) {
        userPreferences.setDarkMode(isDarkMode)
    }
}