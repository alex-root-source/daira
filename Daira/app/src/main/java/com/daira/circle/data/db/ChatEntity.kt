package com.daira.circle.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val initials: String,
    val isGroup: Boolean
)
