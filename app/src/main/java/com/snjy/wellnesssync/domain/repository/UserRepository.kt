package com.snjy.wellnesssync.domain.repository

import com.snjy.wellnesssync.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        age: Int,
        height: Double,
        weight: Double,
        profession: String,
        dietaryPreference: String
    ): Result<User>

    suspend fun loginUser(email: String, password: String): Result<User>

    suspend fun logoutUser(): Result<Unit>

    suspend fun getUserProfile(): Result<User>

    suspend fun updateUserProfile(user: User): Result<User>

    suspend fun isUserLoggedIn(): Flow<Boolean>

    suspend fun getUserThemePreference(): Flow<Boolean> // true for dark mode, false for light mode

    suspend fun setUserThemePreference(isDarkMode: Boolean)
}