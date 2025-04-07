package com.snjy.wellnesssync.domain.model

import java.util.Date

data class ChatMessage(
    val id: String,
    val userId: String,
    val content: String,
    val timestamp: Date,
    val sender: MessageSender
)

enum class MessageSender {
    USER,
    BOT
}

data class ChatConversation(
    val messages: List<ChatMessage>
)