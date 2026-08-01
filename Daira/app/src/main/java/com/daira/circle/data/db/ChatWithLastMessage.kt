package com.daira.circle.data.db

data class ChatWithLastMessage(
    val id: Int,
    val name: String,
    val initials: String,
    val isGroup: Boolean,
    val lastMessageText: String?,
    val lastMessageTime: Long?
)
