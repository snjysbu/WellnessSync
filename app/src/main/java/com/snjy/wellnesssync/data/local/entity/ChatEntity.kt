package com.snjy.wellnesssync.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.snjy.wellnesssync.domain.model.ChatMessage
import com.snjy.wellnesssync.domain.model.MessageSender
import java.util.Date

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val content: String,
    val timestamp: Long, // Timestamp
    val sender: String // "USER" or "BOT"
)

fun ChatEntity.toDomainModel(): ChatMessage {
    return ChatMessage(
        id = id,
        userId = userId,
        content = content,
        timestamp = Date(timestamp),
        sender = MessageSender.valueOf(sender)
    )
}

fun ChatMessage.toEntity(): ChatEntity {
    return ChatEntity(
        id = id,
        userId = userId,
        content = content,
        timestamp = timestamp.time,
        sender = sender.name
    )
}