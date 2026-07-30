package com.daira.circle.ui.screens

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
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted
import com.daira.circle.viewmodel.SocialViewModel

@Composable
fun ChatScreen(viewModel: SocialViewModel, onOpenChat: (FriendEntry) -> Unit) {
    val friends by viewModel.friends.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text("الدردشة", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Text("محادثات حقيقية مع دائرتك", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = TextMuted)
        }

        if (friends.isEmpty()) {
            Text(
                "لسا ما عندك أصدقاء تكلمهم — انضم لدائرة أو شارك رمزك من تبويب \"دائرتك\"",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp)) {
            items(friends, key = { it.uid }) { friend ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenChat(friend) }
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(46.dp).background(Surface2, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(friend.initials, color = Color.White) }

                    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                        Text(friend.displayName, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        Text("اضغط لفتح المحادثة", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
            }
        }
    }
}
