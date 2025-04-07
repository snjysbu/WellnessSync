package com.snjy.wellnesssync.domain.usecase.auth

import com.snjy.wellnesssync.domain.model.User
import com.snjy.wellnesssync.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }

        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }

        return userRepository.loginUser(email, password)
    }
}