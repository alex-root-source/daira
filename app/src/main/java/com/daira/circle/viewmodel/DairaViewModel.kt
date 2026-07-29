package com.daira.circle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.daira.circle.data.DairaRepository
import com.daira.circle.data.db.ChatWithLastMessage
import com.daira.circle.data.db.DairaDatabase
import com.daira.circle.data.db.InviteCodeEntity
import com.daira.circle.data.db.MemberEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DairaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DairaRepository(DairaDatabase.getInstance(application))

    val members: StateFlow<List<MemberEntity>> = repository.members
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeInviteCode: StateFlow<InviteCodeEntity?> = repository.activeInviteCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val chats: StateFlow<List<ChatWithLastMessage>> = repository.chats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun regenerateInviteCode() {
        viewModelScope.launch { repository.regenerateInviteCode() }
    }

    // إرسال رسالة تجريبية فعلية — تُخزَّن في SQLite وتبقى موجودة حتى بعد إغلاق التطبيق
    fun sendDemoMessage(chatId: Int) {
        viewModelScope.launch {
            repository.sendMessage(chatId, "👍 رسالة تجريبية محفوظة فعليًا")
        }
    }
}
