package com.snjy.wellnesssync.data.remote.datasource

import android.util.Log
import com.snjy.wellnesssync.data.remote.api.SupabaseService
import com.snjy.wellnesssync.data.remote.dto.UserDto
import com.snjy.wellnesssync.data.remote.dto.toDomainModel
import com.snjy.wellnesssync.domain.model.User
import java.util.UUID
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val supabaseService: SupabaseService
) {
    private val TAG = "UserRemoteDataSource"

    suspend fun registerUser(userDto: UserDto): Result<UserDto> {
        return try {
            Log.d(TAG, "Attempting to register user: ${userDto.email}")
            val response = supabaseService.registerUser(userDto)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "Registration successful")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Registration failed: ${response.code()} - ${response.message()}")
                if (response.errorBody() != null) {
                    Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                }
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during registration", e)
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<String> {
        return try {
            Log.d(TAG, "Attempting to login user: $email")
            val credentials = mapOf(
                "email" to email,
                "password" to password
            )

            val response = supabaseService.loginUser(credentials)
            if (response.isSuccessful && response.body() != null) {
                val responseBody = response.body()!!
                val token = responseBody["access_token"] as? String

                if (token != null) {
                    Log.d(TAG, "Login successful")
                    Result.success(token)
                } else {
                    Log.e(TAG, "Login failed: Token not found in response")
                    Result.failure(Exception("Login failed: Token not found"))
                }
            } else {
                Log.e(TAG, "Login failed: ${response.code()} - ${response.message()}")
                if (response.errorBody() != null) {
                    Log.e(TAG, "Error body: ${response.errorBody()?.string()}")
                }
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during login", e)
            Result.failure(e)
        }
    }

    suspend fun logoutUser(token: String): Result<Unit> {
        return try {
            Log.d(TAG, "Attempting to logout user with token")
            val response = supabaseService.logoutUser("Bearer $token")

            if (response.isSuccessful) {
                Log.d(TAG, "Logout successful")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Logout failed: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Logout failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during logout", e)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(token: String, userId: String): Result<UserDto> {
        return try {
            Log.d(TAG, "Fetching user profile for ID: $userId")
            val response = supabaseService.getUserProfile("Bearer $token", userId)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "User profile retrieved successfully")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Failed to get user profile: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Failed to get user profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception when fetching user profile", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(token: String, userDto: UserDto): Result<UserDto> {
        return try {
            Log.d(TAG, "Updating user profile for ID: ${userDto.id}")
            val response = supabaseService.updateUserProfile("Bearer $token", userDto)

            if (response.isSuccessful && response.body() != null) {
                Log.d(TAG, "User profile updated successfully")
                Result.success(response.body()!!)
            } else {
                Log.e(TAG, "Failed to update user profile: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Failed to update user profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception when updating user profile", e)
            Result.failure(e)
        }
    }
}