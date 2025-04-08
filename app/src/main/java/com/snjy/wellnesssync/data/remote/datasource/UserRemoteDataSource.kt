package com.snjy.wellnesssync.data.remote.datasource

import android.util.Log
import com.snjy.wellnesssync.data.remote.api.SupabaseService
import com.snjy.wellnesssync.data.remote.dto.UserDto
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val supabaseService: SupabaseService
) {
    private val tag = "UserRemoteDataSource" // Fixed capitalization

    suspend fun registerUser(credentials: Map<String, String>): Result<Map<String, Any>> {
        return try {
            Log.d(tag, "Attempting to register user: ${credentials["email"]}")
            val response = supabaseService.registerUser(credentials)
            Log.d(tag, "Registration response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(tag, "Registration successful")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                val errorMsg = "Registration failed: ${response.message()} - $errorBody"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Registration exception", e)
            Result.failure(e)
        }
    }

    suspend fun createUserProfile(token: String, userDto: UserDto): Result<UserDto> {
        return try {
            Log.d(tag, "Creating user profile for ID: ${userDto.id}")
            // Fix: Pass parameters in the correct order
            val response = supabaseService.createUserProfile(token, "return=representation", userDto)
            Log.d(tag, "CreateUserProfile response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(tag, "User profile created successfully")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                val errorMsg = "Failed to create user profile: ${response.message()} - $errorBody"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "CreateUserProfile exception", e)
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            Log.d(tag, "Attempting to login user: $email")
            val credentials = mapOf(
                "email" to email,
                "password" to password
            )
            val response = supabaseService.loginUser(credentials)
            Log.d(tag, "Login response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val token = responseBody["access_token"] as? String
                if (token != null) {
                    Log.d(tag, "Login successful, token received")
                    Result.success(token)
                } else {
                    val errorMsg = "Login failed: Token not found in response"
                    Log.e(tag, errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                val errorMsg = "Login failed: ${response.message()} - $errorBody"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Login exception", e)
            Result.failure(e)
        }
    }

    suspend fun logoutUser(token: String): Result<Unit> {
        return try {
            Log.d(tag, "Attempting to logout user")
            val response = supabaseService.logoutUser("Bearer $token")
            Log.d(tag, "Logout response code: ${response.code()}")

            if (response.isSuccessful) {
                Log.d(tag, "Logout successful")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                val errorMsg = "Logout failed: ${response.message()} - $errorBody"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "Logout exception", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(token: String, userId: String): Result<List<UserDto>> {
        return try {
            Log.d(tag, "Fetching user profile for ID: $userId")
            val response = supabaseService.getUserProfile("Bearer $token", userId)
            Log.d(tag, "GetUserProfile response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(tag, "User profile fetched successfully")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                val errorMsg = "Failed to get user profile: ${response.message()} - $errorBody"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "GetUserProfile exception", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(token: String, userDto: UserDto, userId: String): Result<UserDto> {
        return try {
            Log.d(tag, "Updating user profile for ID: $userId")
            // Fix: Pass parameters in the correct order and type
            val response = supabaseService.updateUserProfile(
                token = "Bearer $token",
                prefer = "return=representation",
                user = userDto,
                userId = userId
            )
            Log.d(tag, "UpdateUserProfile response code: ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                Log.d(tag, "User profile updated successfully")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                val errorMsg = "Failed to update user profile: ${response.message()} - $errorBody"
                Log.e(tag, errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e(tag, "UpdateUserProfile exception", e)
            Result.failure(e)
        }
    }
}