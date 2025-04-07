package com.snjy.wellnesssync.data.repository

import com.snjy.wellnesssync.data.local.dao.ChatDao
import com.snjy.wellnesssync.data.local.entity.toEntity
import com.snjy.wellnesssync.data.local.entity.toDomainModel
import com.snjy.wellnesssync.data.remote.datasource.ChatRemoteDataSource
import com.snjy.wellnesssync.data.remote.dto.toDomainModel
import com.snjy.wellnesssync.domain.model.ChatMessage
import com.snjy.wellnesssync.domain.model.MessageSender
import com.snjy.wellnesssync.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val chatRemoteDataSource: ChatRemoteDataSource
) : ChatRepository {

    override suspend fun sendMessage(userId: String, message: String): Result<ChatMessage> {
        // Create and save user message
        val userMessageId = UUID.randomUUID().toString()
        val userMessage = ChatMessage(
            id = userMessageId,
            userId = userId,
            content = message,
            timestamp = Date(),
            sender = MessageSender.USER
        )
        chatDao.insertChatMessage(userMessage.toEntity())

        // Send message to Gemini API
        return chatRemoteDataSource.sendMessage(userId, message).map { botMessageDto ->
            val botMessage = botMessageDto.toDomainModel()
            // Save bot message to local DB
            chatDao.insertChatMessage(botMessage.toEntity())
            botMessage
        }
    }

    override fun getChatHistory(userId: String): Flow<List<ChatMessage>> {
        return chatDao.getChatHistory(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun clearChatHistory(userId: String): Result<Unit> {
        return try {
            chatDao.clearChatHistory(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}