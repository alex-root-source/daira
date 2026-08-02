package com.daira.circle.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daira.circle.data.cloudinary.CloudinaryUploader
import com.daira.circle.data.firestore.ChatMessage
import com.daira.circle.data.firestore.ChatMeta
import com.daira.circle.data.firestore.FirestoreRepository
import com.daira.circle.data.firestore.FriendEntry
import com.daira.circle.data.firestore.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// نموذج جاهز للعرض بشاشة قائمة الدردشات: يجمع بيانات الصديق مع ملخص آخر محادثة
data class ChatPreviewUi(
    val friend: FriendEntry,
    val lastMessageText: String,
    val lastMessageAt: Long,
    val unreadForMe: Long
)

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirestoreRepository(FirebaseAuth.getInstance())
    private val uploader = CloudinaryUploader(application)

    val profile: StateFlow<UserProfile?> = repository.observeMyProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val friends: StateFlow<List<FriendEntry>> = repository.observeFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val chatsMeta: StateFlow<Map<String, ChatMeta>> = repository.observeChatsMeta()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val chatPreviews: StateFlow<List<ChatPreviewUi>> = combine(friends, chatsMeta) { friendList, metaMap ->
        friendList.map { friend ->
            val meta = metaMap[friend.uid]
            ChatPreviewUi(
                friend = friend,
                lastMessageText = meta?.lastMessageText ?: "",
                lastMessageAt = meta?.lastMessageAt ?: 0L,
                unreadForMe = meta?.unreadForMe ?: 0L
            )
        }.sortedByDescending { it.lastMessageAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _joinFeedback = MutableStateFlow<String?>(null)
    val joinFeedback: StateFlow<String?> = _joinFeedback.asStateFlow()

    private val _openChatWith = MutableStateFlow<FriendEntry?>(null)
    val openChatWith: StateFlow<FriendEntry?> = _openChatWith.asStateFlow()

    val currentMessages: StateFlow<List<ChatMessage>> = _openChatWith
        .flatMapLatest { friend ->
            if (friend == null) flowOf(emptyList()) else repository.observeMessages(friend.uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isUploadingMedia = MutableStateFlow(false)
    val isUploadingMedia: StateFlow<Boolean> = _isUploadingMedia.asStateFlow()

    private val _mediaError = MutableStateFlow<String?>(null)
    val mediaError: StateFlow<String?> = _mediaError.asStateFlow()

    fun ensureProfileReady(email: String) {
        viewModelScope.launch { repository.ensureUserProfile(email) }
    }

    fun regenerateInviteCode() {
        val current = profile.value?.activeInviteCode ?: return
        viewModelScope.launch { repository.regenerateInviteCode(current) }
    }

    fun joinByCode(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            try {
                val name = repository.joinByInviteCode(code)
                _joinFeedback.value = "انضممت لدائرة $name بنجاح 🎉"
            } catch (e: Exception) {
                _joinFeedback.value = e.message ?: "صار خطأ، حاول مرة ثانية"
            }
        }
    }

    fun clearJoinFeedback() {
        _joinFeedback.value = null
    }

    fun openChat(friend: FriendEntry) {
        _openChatWith.value = friend
        viewModelScope.launch { repository.markChatRead(friend.uid) }
    }

    fun closeChat() {
        _openChatWith.value = null
    }

    fun sendMessage(text: String) {
        val friend = _openChatWith.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch { repository.sendMessage(friend.uid, text) }
    }

    fun sendMedia(uri: Uri, mediaType: String) {
        val friend = _openChatWith.value ?: return
        viewModelScope.launch {
            _isUploadingMedia.value = true
            try {
                val url = uploader.upload(uri, mediaType)
                repository.sendMediaMessage(friend.uid, url, mediaType)
            } catch (e: Exception) {
                _mediaError.value = e.message ?: "فشل رفع الملف، تأكد من اتصالك بالإنترنت"
            } finally {
                _isUploadingMedia.value = false
            }
        }
    }

    fun clearMediaError() {
        _mediaError.value = null
    }

    fun toggleMute(friend: FriendEntry) {
        viewModelScope.launch { repository.toggleMute(friend.uid, !friend.muted) }
    }

    /** يزيل الصداقة، ويقفل شاشة المحادثة تلقائيًا لو كانت مفتوحة مع نفس الشخص */
    fun removeFriend(friend: FriendEntry) {
        viewModelScope.launch {
            repository.removeFriend(friend.uid)
            if (_openChatWith.value?.uid == friend.uid) _openChatWith.value = null
        }
    }

    fun blockFriend(friend: FriendEntry) {
        viewModelScope.launch {
            repository.blockUser(friend.uid)
            if (_openChatWith.value?.uid == friend.uid) _openChatWith.value = null
        }
    }

    fun clearConversation(friend: FriendEntry) {
        viewModelScope.launch { repository.clearConversation(friend.uid) }
    }

    fun deleteMessage(messageId: String) {
        val friend = _openChatWith.value ?: return
        viewModelScope.launch { repository.deleteMessage(friend.uid, messageId) }
    }

    fun myUid(): String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
}
