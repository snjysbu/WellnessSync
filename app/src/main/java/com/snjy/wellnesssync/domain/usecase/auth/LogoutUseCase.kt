package com.snjy.wellnesssync.domain.usecase.auth

import com.snjy.wellnesssync.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return userRepository.logoutUser()
    }
}