package com.snjy.wellnesssync.data.remote.datasource

import com.snjy.wellnesssync.data.remote.api.GeminiContent
import com.snjy.wellnesssync.data.remote.api.GeminiPart
import com.snjy.wellnesssync.data.remote.api.GeminiRequest
import com.snjy.wellnesssync.data.remote.api.GeminiService
import com.snjy.wellnesssync.data.remote.dto.ChatDto
import com.snjy.wellnesssync.domain.model.MessageSender
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val geminiService: GeminiService
) {
    // This would normally be stored securely, not hardcoded
    private val GEMINI_API_KEY = "AIzaSyAHfactIHhF6k7vG8O0K4l0FERyy8ahgPM"

    suspend fun sendMessage(userId: String, message: String): Result<ChatDto> {
        return try {
            // Create user message DTO
            val userMessageId = UUID.randomUUID().toString()
            val userMessage = ChatDto(
                id = userMessageId,
                userId = userId,
                content = message,
                timestamp = Date().time,
                sender = MessageSender.USER.name
            )

            // Send to Gemini API and get response
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = message))
                    )
                )
            )

            val response = geminiService.generateContent(GEMINI_API_KEY, request)

            if (response.isSuccessful && response.body() != null) {
                val geminiResponse = response.body()!!
                val botResponse = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "I couldn't generate a response at this time. Please try again later."

                // Create bot message DTO
                val botMessageId = UUID.randomUUID().toString()
                val botMessage = ChatDto(
                    id = botMessageId,
                    userId = userId,
                    content = botResponse,
                    timestamp = Date().time,
                    sender = MessageSender.BOT.name
                )

                Result.success(botMessage)
            } else {
                Result.failure(Exception("Failed to get response from AI: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}