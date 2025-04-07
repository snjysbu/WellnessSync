package com.snjy.wellnesssync.domain.usecase.auth

import com.snjy.wellnesssync.domain.model.User
import com.snjy.wellnesssync.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        age: Int,
        height: Double,
        weight: Double,
        profession: String,
        dietaryPreference: String
    ): Result<User> {
        // Basic validation
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name cannot be empty"))
        }

        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }

        if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }

        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }

        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        if (age <= 0) {
            return Result.failure(IllegalArgumentException("Age must be greater than 0"))
        }

        if (height <= 0) {
            return Result.failure(IllegalArgumentException("Height must be greater than 0"))
        }

        if (weight <= 0) {
            return Result.failure(IllegalArgumentException("Weight must be greater than 0"))
        }

        if (profession.isBlank()) {
            return Result.failure(IllegalArgumentException("Profession cannot be empty"))
        }

        if (dietaryPreference != "VEGETARIAN" && dietaryPreference != "NON_VEGETARIAN") {
            return Result.failure(IllegalArgumentException("Invalid dietary preference"))
        }

        return userRepository.registerUser(
            name = name,
            email = email,
            password = password,
            age = age,
            height = height,
            weight = weight,
            profession = profession,
            dietaryPreference = dietaryPreference
        )
    }
}