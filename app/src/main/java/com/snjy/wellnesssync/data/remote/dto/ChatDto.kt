package com.snjy.wellnesssync.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.snjy.wellnesssync.domain.model.ChatMessage
import com.snjy.wellnesssync.domain.model.MessageSender
import java.util.Date

data class ChatDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("sender")
    val sender: String
)

fun ChatDto.toDomainModel(): ChatMessage {
    return ChatMessage(
        id = id,
        userId = userId,
        content = content,
        timestamp = Date(timestamp),
        sender = MessageSender.valueOf(sender)
    )
}

fun ChatMessage.toDto(): ChatDto {
    return ChatDto(
        id = id,
        userId = userId,
        content = content,
        timestamp = timestamp.time,
        sender = sender.name
    )
}