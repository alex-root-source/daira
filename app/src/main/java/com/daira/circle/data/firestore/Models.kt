package com.daira.circle.data.firestore

// ملاحظة: كل الحقول لها قيم افتراضية لأن Firestore يحتاجها عند التحويل التلقائي (toObject)

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val initials: String = "",
    val activeInviteCode: String = "",
    val createdAtMillis: Long = 0L
)

data class FriendEntry(
    val uid: String = "",
    val displayName: String = "",
    val initials: String = "",
    val sinceLabel: String = ""
)

data class ChatMessage(
    val id: String = "",
    val senderUid: String = "",
    val text: String = "",
    val timestampMillis: Long = 0L
)
