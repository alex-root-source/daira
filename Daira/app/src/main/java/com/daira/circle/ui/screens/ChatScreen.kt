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
import com.daira.circle.data.firestore.FriendEntry
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted
import com.daira.circle.viewmodel.ChatPreviewUi
import com.daira.circle.viewmodel.SocialViewModel

@Composable
fun ChatScreen(viewModel: SocialViewModel, onOpenChat: (FriendEntry) -> Unit) {
    val chatPreviews by viewModel.chatPreviews.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text("الدردشة", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Text("محادثات حقيقية مع دائرتك", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = TextMuted)
        }

        if (chatPreviews.isEmpty()) {
            Text(
                "لسا ما عندك أصدقاء تكلمهم — انضم لدائرة أو شارك رمزك من تبويب \"دائرتك\"",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp)) {
            items(chatPreviews, key = { it.friend.uid }) { preview ->
                ChatRow(preview, onClick = { onOpenChat(preview.friend) })
            }
        }
    }
}

@Composable
private fun ChatRow(preview: ChatPreviewUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(46.dp).background(Surface2, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(preview.friend.initials, color = Color.White) }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(preview.friend.displayName, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(
                preview.lastMessageText.ifBlank { "اضغط لبدء المحادثة" },
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 1
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            if (preview.lastMessageAt > 0) {
                Text(
                    DateUtils.getRelativeTimeSpanString(preview.lastMessageAt).toString(),
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            if (preview.unreadForMe > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(Peach, CircleShape)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(preview.unreadForMe.toString(), color = Color(0xFF241A16), style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
