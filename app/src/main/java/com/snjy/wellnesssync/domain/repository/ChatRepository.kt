package com.snjy.wellnesssync.domain.repository

import com.snjy.wellnesssync.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(userId: String, message: String): Result<ChatMessage>

    fun getChatHistory(userId: String): Flow<List<ChatMessage>>

    suspend fun clearChatHistory(userId: String): Result<Unit>
}