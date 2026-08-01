package com.daira.circle.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestampMillis ASC")
    fun observeMessagesForChat(chatId: Int): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(message: MessageEntity)
}
