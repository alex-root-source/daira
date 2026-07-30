package com.daira.circle.data.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirestoreRepository(private val auth: FirebaseAuth) {

    private val db = FirebaseFirestore.getInstance()
    private val usersCol = db.collection("users")
    private val inviteCodesCol = db.collection("inviteCodes")

    private val myUid: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("لا يوجد مستخدم مسجّل دخول")

    /**
     * يُستدعى بعد كل تسجيل دخول ناجح.
     * لو أول مرة لهذا المستخدم، ينشئ ملفه الشخصي ورمز دعوة أول.
     * لو موجود مسبقًا، ما يغيّر شيء.
     */
    suspend fun ensureUserProfile(email: String) {
        val doc = usersCol.document(myUid).get().await()
        if (doc.exists()) return

        val code = randomCode()
        val displayName = email.substringBefore("@")
        val initials = displayName.take(2)

        val profile = UserProfile(
            uid = myUid,
            email = email,
            displayName = displayName,
            initials = initials,
            activeInviteCode = code,
            createdAtMillis = System.currentTimeMillis()
        )

        db.runBatch { batch ->
            batch.set(usersCol.document(myUid), profile)
            batch.set(inviteCodesCol.document(code), mapOf("ownerUid" to myUid))
        }.await()
    }

    fun observeMyProfile(): Flow<UserProfile?> = callbackFlow {
        val registration = usersCol.document(myUid).addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.toObject(UserProfile::class.java))
        }
        awaitClose { registration.remove() }
    }

    /** يولّد رمز دعوة جديدًا ويحذف/يبطل الرمز القديم في نفس العملية (Transaction) */
    suspend fun regenerateInviteCode(oldCode: String) {
        val newCode = randomCode()
        db.runTransaction { tx ->
            if (oldCode.isNotBlank()) {
                tx.delete(inviteCodesCol.document(oldCode))
            }
            tx.set(inviteCodesCol.document(newCode), mapOf("ownerUid" to myUid))
            tx.update(usersCol.document(myUid), "activeInviteCode", newCode)
        }.await()
    }

    /**
     * ينضم لدائرة صاحب هذا الرمز — يضيف صداقة متبادلة (Mutual) بين الاثنين.
     * يرجع اسم الصديق عند النجاح، أو يرمي استثناء برسالة عربية عند الفشل.
     */
    suspend fun joinByInviteCode(code: String): String {
        val codeDoc = inviteCodesCol.document(code.trim().uppercase()).get().await()
        if (!codeDoc.exists()) throw IllegalArgumentException("رمز الدعوة غير صحيح أو انتهى")

        val ownerUid = codeDoc.getString("ownerUid") ?: throw IllegalArgumentException("رمز غير صالح")
        if (ownerUid == myUid) throw IllegalArgumentException("لا يمكنك استخدام رمزك الخاص")

        val ownerDoc = usersCol.document(ownerUid).get().await()
        val owner = ownerDoc.toObject(UserProfile::class.java)
            ?: throw IllegalArgumentException("تعذر العثور على صاحب هذا الرمز")

        val myDoc = usersCol.document(myUid).get().await()
        val me = myDoc.toObject(UserProfile::class.java)
            ?: throw IllegalStateException("ملفك الشخصي غير جاهز بعد")

        val existing = usersCol.document(myUid).collection("friends").document(ownerUid).get().await()
        if (existing.exists()) throw IllegalArgumentException("أنتما أصدقاء بالفعل")

        val since = "صديق منذ الآن"
        db.runBatch { batch ->
            batch.set(
                usersCol.document(myUid).collection("friends").document(ownerUid),
                FriendEntry(owner.uid, owner.displayName, owner.initials, since)
            )
            batch.set(
                usersCol.document(ownerUid).collection("friends").document(myUid),
                FriendEntry(me.uid, me.displayName, me.initials, since)
            )
        }.await()

        return owner.displayName
    }

    fun observeFriends(): Flow<List<FriendEntry>> = callbackFlow {
        val registration = usersCol.document(myUid).collection("friends")
            .addSnapshotListener { snapshot, _ ->
                val friends = snapshot?.documents?.mapNotNull { it.toObject(FriendEntry::class.java) } ?: emptyList()
                trySend(friends)
            }
        awaitClose { registration.remove() }
    }

    /** معرّف محادثة ثابت ومشترك بين شخصين، بغض النظر عن ترتيبهم */
    private fun chatIdWith(otherUid: String): String =
        listOf(myUid, otherUid).sorted().joinToString("_")

    fun observeMessages(otherUid: String): Flow<List<ChatMessage>> = callbackFlow {
        val registration = db.collection("chats").document(chatIdWith(otherUid))
            .collection("messages")
            .orderBy("timestampMillis")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { registration.remove() }
    }

    suspend fun sendMessage(otherUid: String, text: String) {
        val chatId = chatIdWith(otherUid)
        val message = ChatMessage(
            senderUid = myUid,
            text = text,
            timestampMillis = System.currentTimeMillis()
        )
        db.collection("chats").document(chatId).collection("messages").add(message).await()
        // نحدّث وثيقة المحادثة نفسها بمعلومات بسيطة (يفيد لاحقًا لعرض آخر رسالة بسرعة)
        db.collection("chats").document(chatId).set(
            mapOf("participants" to listOf(myUid, otherUid), "lastMessageAt" to message.timestampMillis)
        ).await()
    }

    private fun randomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        fun chunk() = (1..3).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        return "${chunk()}-${chunk()}"
    }
}
