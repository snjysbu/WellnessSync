package com.snjy.wellnesssync.domain.usecase.chat

import com.snjy.wellnesssync.domain.model.ChatMessage
import com.snjy.wellnesssync.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(userId: String): Flow<List<ChatMessage>> {
        return chatRepository.getChatHistory(userId)
    }

    suspend fun clear(userId: String): Result<Unit> {
        return chatRepository.clearChatHistory(userId)
    }
}