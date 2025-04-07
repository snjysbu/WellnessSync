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
        // Create a UserDto to pass to the remote data source
        val userDto = UserDto(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            age = age,
            height = height,
            weight = weight,
            profession = profession,
            dietaryPreference = dietaryPreference,
            profileImageUrl = null
        )

        return userRemoteDataSource.registerUser(userDto).map { registeredUserDto ->
            // Convert DTO to domain model
            val user = registeredUserDto.toDomainModel()

            // Save user to local database
            userDao.insertUser(user.toEntity())

            // After successful registration, perform login
            userRemoteDataSource.loginUser(email, password).fold(
                onSuccess = { token ->
                    userPreferences.saveUserSession(user.id, token)
                },
                onFailure = {
                    Log.e(TAG, "Login failed after registration: ${it.message}")
                }
            )

            user
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<User> {
        return userRemoteDataSource.loginUser(email, password).map { token ->
            // Get or create user in local DB
            val localUser = userDao.getUserByEmail(email)

            val user = if (localUser != null) {
                localUser.toDomainModel()
            } else {
                // Fetch user details from remote
                val result = userPreferences.userId.first()?.let { userId ->
                    userRemoteDataSource.getUserProfile(token, userId)
                }

                if (result != null) {
                    val remoteUser = result.getOrNull()
                    if (remoteUser != null) {
                        val domainUser = remoteUser.toDomainModel()
                        userDao.insertUser(domainUser.toEntity())
                        domainUser
                    } else {
                        // Create a placeholder user with minimal info
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
                        placeholderUser
                    }
                } else {
                    // Create a placeholder user with minimal info
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
                    placeholderUser
                }
            }

            // Save user session
            userPreferences.saveUserSession(user.id, token)

            user
        }
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
        return userRemoteDataSource.getUserProfile(token, userId).map { userDto ->
            val user = userDto.toDomainModel()
            userDao.insertUser(user.toEntity())
            user
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        val token = userPreferences.authToken.first() ?: return Result.failure(Exception("User not logged in"))

        // Convert domain model to DTO for the remote data source
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

        return userRemoteDataSource.updateUserProfile(token, userDto).map { remoteUserDto ->
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