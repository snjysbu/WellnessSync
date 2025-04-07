package com.snjy.wellnesssync.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.snjy.wellnesssync.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(chatMessage: ChatEntity)

    @Query("SELECT * FROM chat_messages WHERE userId = :userId ORDER BY timestamp ASC")
    fun getChatHistory(userId: String): Flow<List<ChatEntity>>

    @Query("DELETE FROM chat_messages WHERE userId = :userId")
    suspend fun clearChatHistory(userId: String)
}