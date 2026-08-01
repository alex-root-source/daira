package com.daira.circle.data

import androidx.room.withTransaction
import com.daira.circle.data.db.ChatWithLastMessage
import com.daira.circle.data.db.DairaDatabase
import com.daira.circle.data.db.InviteCodeEntity
import com.daira.circle.data.db.MemberEntity
import com.daira.circle.data.db.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class DairaRepository(private val db: DairaDatabase) {

    val members: Flow<List<MemberEntity>> = db.memberDao().observeAll()
    val activeInviteCode: Flow<InviteCodeEntity?> = db.inviteCodeDao().observeActiveCode()
    val chats: Flow<List<ChatWithLastMessage>> = db.chatDao().observeChatsWithLastMessage()

    /**
     * يولّد رمز دعوة جديدًا ويُبطل الرمز الفعّال السابق في نفس العملية (Transaction)،
     * بحيث لا يوجد أبدًا أكثر من رمز فعّال واحد لكل مستخدم في نفس اللحظة.
     */
    suspend fun regenerateInviteCode() {
        db.withTransaction {
            db.inviteCodeDao().deactivateAll()
            db.inviteCodeDao().insert(
                InviteCodeEntity(
                    code = randomCode(),
                    createdAtMillis = System.currentTimeMillis(),
                    isActive = true
                )
            )
        }
    }

    suspend fun sendMessage(chatId: Int, text: String, isMine: Boolean = true) {
        db.messageDao().insert(
            MessageEntity(
                chatId = chatId,
                text = text,
                timestampMillis = System.currentTimeMillis(),
                isMine = isMine
            )
        )
    }

    private fun randomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        fun chunk() = (1..3).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        return "${chunk()}-${chunk()}"
    }
}
