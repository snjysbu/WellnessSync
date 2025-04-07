package com.snjy.wellnesssync.domain.usecase.chat

import com.snjy.wellnesssync.domain.model.ChatMessage
import com.snjy.wellnesssync.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: String, message: String): Result<ChatMessage> {
        if (message.isBlank()) {
            return Result.failure(IllegalArgumentException("Message cannot be empty"))
        }

        return chatRepository.sendMessage(userId, message)
    }
}