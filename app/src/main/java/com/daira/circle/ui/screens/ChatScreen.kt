package com.daira.circle.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daira.circle.data.db.ChatWithLastMessage
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted
import com.daira.circle.viewmodel.DairaViewModel

@Composable
fun ChatScreen(viewModel: DairaViewModel) {
    val chats by viewModel.chats.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text("الدردشة", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Text("محادثات دائرتك فقط — محفوظة فعليًا على جهازك", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = TextMuted)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(chats, key = { it.id }) { chat ->
                ChatRow(chat, onClick = { viewModel.sendDemoMessage(chat.id) })
            }
        }
    }
}

@Composable
private fun ChatRow(chat: ChatWithLastMessage, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // اضغط على أي محادثة لتجربة إرسال رسالة تُحفظ فعليًا
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(46.dp)
                .background(Surface2, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(chat.initials, color = Color.White) }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(chat.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(
                chat.lastMessageText ?: "لا رسائل بعد — اضغط للتجربة",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 1
            )
        }

        Text(
            chat.lastMessageTime?.let { formatRelativeTime(it) } ?: "",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

private fun formatRelativeTime(millis: Long): String =
    DateUtils.getRelativeTimeSpanString(millis).toString()
