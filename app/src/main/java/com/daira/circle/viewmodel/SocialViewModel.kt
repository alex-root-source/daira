package com.daira.circle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daira.circle.data.firestore.ChatMessage
import com.daira.circle.data.firestore.FirestoreRepository
import com.daira.circle.data.firestore.FriendEntry
import com.daira.circle.data.firestore.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SocialViewModel : ViewModel() {

    private val repository = FirestoreRepository(FirebaseAuth.getInstance())

    val profile: StateFlow<UserProfile?> = repository.observeMyProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val friends: StateFlow<List<FriendEntry>> = repository.observeFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _joinFeedback = MutableStateFlow<String?>(null)
    val joinFeedback: StateFlow<String?> = _joinFeedback.asStateFlow()

    // الصديق المفتوحة معه المحادثة حاليًا (null يعني نعرض قائمة المحادثات)
    private val _openChatWith = MutableStateFlow<FriendEntry?>(null)
    val openChatWith: StateFlow<FriendEntry?> = _openChatWith.asStateFlow()

    val currentMessages: StateFlow<List<ChatMessage>> = _openChatWith
        .flatMapLatest { friend ->
            if (friend == null) kotlinx.coroutines.flow.flowOf(emptyList())
            else repository.observeMessages(friend.uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
    }

    fun closeChat() {
        _openChatWith.value = null
    }

    fun sendMessage(text: String) {
        val friend = _openChatWith.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch { repository.sendMessage(friend.uid, text) }
    }

    fun myUid(): String = FirebaseAuth.getInstance().currentUser?.uid ?: ""
}
