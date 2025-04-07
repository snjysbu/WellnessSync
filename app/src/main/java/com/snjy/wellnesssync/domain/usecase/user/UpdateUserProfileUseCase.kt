package com.snjy.wellnesssync.domain.usecase.user

import com.snjy.wellnesssync.domain.model.User
import com.snjy.wellnesssync.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<User> {
        // Basic validation
        if (user.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name cannot be empty"))
        }

        if (user.age <= 0) {
            return Result.failure(IllegalArgumentException("Age must be greater than 0"))
        }

        if (user.height <= 0) {
            return Result.failure(IllegalArgumentException("Height must be greater than 0"))
        }

        if (user.weight <= 0) {
            return Result.failure(IllegalArgumentException("Weight must be greater than 0"))
        }

        if (user.profession.isBlank()) {
            return Result.failure(IllegalArgumentException("Profession cannot be empty"))
        }

        return userRepository.updateUserProfile(user)
    }
}