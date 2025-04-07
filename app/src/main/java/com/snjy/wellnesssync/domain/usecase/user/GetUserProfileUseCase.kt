package com.snjy.wellnesssync.domain.usecase.user

import com.snjy.wellnesssync.domain.model.User
import com.snjy.wellnesssync.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        return userRepository.getUserProfile()
    }
}