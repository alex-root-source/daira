package com.daira.circle.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert
    suspend fun insert(chat: ChatEntity): Long

    // يجلب كل محادثة مع نص ووقت آخر رسالة فيها بجلب واحد فقط (بدون N+1 استعلام)
    @Query(
        """
        SELECT c.id as id, c.name as name, c.initials as initials, c.isGroup as isGroup,
               (SELECT text FROM messages m WHERE m.chatId = c.id ORDER BY m.timestampMillis DESC LIMIT 1) as lastMessageText,
               (SELECT timestampMillis FROM messages m WHERE m.chatId = c.id ORDER BY m.timestampMillis DESC LIMIT 1) as lastMessageTime
        FROM chats c
        ORDER BY lastMessageTime DESC
        """
    )
    fun observeChatsWithLastMessage(): Flow<List<ChatWithLastMessage>>
}
