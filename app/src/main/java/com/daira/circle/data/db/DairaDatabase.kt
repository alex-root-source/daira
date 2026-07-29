package com.daira.circle.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

@Database(
    entities = [MemberEntity::class, InviteCodeEntity::class, ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DairaDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun inviteCodeDao(): InviteCodeDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile private var INSTANCE: DairaDatabase? = null

        fun getInstance(context: Context): DairaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, DairaDatabase::class.java, "daira.db")
                    .addCallback(object : RoomDatabase.Callback() {
                        // يُستدعى مرة واحدة فقط، عند إنشاء ملف قاعدة البيانات لأول مرة على الجهاز
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { seedInitialData(it) }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private fun randomCode(): String {
            val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            fun chunk() = (1..3).map { chars[Random.nextInt(chars.length)] }.joinToString("")
            return "${chunk()}-${chunk()}"
        }

        private suspend fun seedInitialData(db: DairaDatabase) {
            val now = System.currentTimeMillis()

            db.inviteCodeDao().insert(
                InviteCodeEntity(code = randomCode(), createdAtMillis = now, isActive = true)
            )

            db.memberDao().insert(MemberEntity(name = "ريم القاسم", initials = "ريم", sinceLabel = "صديقة منذ مارس ٢٠٢٤", joinedAtMillis = now))
            db.memberDao().insert(MemberEntity(name = "سعد المطيري", initials = "سعد", sinceLabel = "صديق منذ يونيو ٢٠٢٤", joinedAtMillis = now))
            db.memberDao().insert(MemberEntity(name = "لينا حداد", initials = "لينا", sinceLabel = "صديقة منذ يناير ٢٠٢٥", joinedAtMillis = now))
            db.memberDao().insert(MemberEntity(name = "عمر شاهين", initials = "عمر", sinceLabel = "صديق منذ أبريل ٢٠٢٥", joinedAtMillis = now))

            val chatIds = listOf(
                db.chatDao().insert(ChatEntity(name = "ريم القاسم", initials = "ريم", isGroup = false)),
                db.chatDao().insert(ChatEntity(name = "عمر شاهين", initials = "عمر", isGroup = false)),
                db.chatDao().insert(ChatEntity(name = "🎉 شلة الجمعة", initials = "شلة", isGroup = true)),
                db.chatDao().insert(ChatEntity(name = "سعد المطيري", initials = "سعد", isGroup = false)),
            )

            db.messageDao().insert(MessageEntity(chatId = chatIds[0].toInt(), text = "شكلها حلوة، نطلع بكرة؟", timestampMillis = now - 60_000, isMine = false))
            db.messageDao().insert(MessageEntity(chatId = chatIds[1].toInt(), text = "وصلت البيت الحين 👍", timestampMillis = now - 3_600_000, isMine = false))
            db.messageDao().insert(MessageEntity(chatId = chatIds[2].toInt(), text = "أنا موجودة أكيد", timestampMillis = now - 7_200_000, isMine = false))
            db.messageDao().insert(MessageEntity(chatId = chatIds[3].toInt(), text = "هلا، شفت اللي بعثتلك؟", timestampMillis = now - 90_000_000, isMine = false))
        }
    }
}
