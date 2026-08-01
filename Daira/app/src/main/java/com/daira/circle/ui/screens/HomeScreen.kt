package com.daira.circle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daira.circle.data.DummyData
import com.daira.circle.data.Post
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.Sage
import com.daira.circle.ui.theme.Surface
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted

@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("دائرتك", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Text("٦ أصدقاء مقرّبون", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
            Icon(Icons.Filled.Notifications, contentDescription = "الإشعارات", tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { StoryBubble("أنت", isAdd = true) }
            item { StoryBubble("ريم") }
            item { StoryBubble("سعد") }
            item { StoryBubble("لينا", seen = true) }
            item { StoryBubble("عمر") }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(DummyData.posts) { post -> PostCard(post) }
        }
    }
}

@Composable
private fun StoryBubble(name: String, isAdd: Boolean = false, seen: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .border(
                    width = 2.dp,
                    color = if (isAdd) TextMuted else if (seen) TextMuted.copy(alpha = 0.3f) else Peach,
                    shape = CircleShape
                )
                .padding(3.dp)
                .background(Surface2, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isAdd) {
                Icon(Icons.Filled.Add, contentDescription = "أضف قصة", tint = TextMuted)
            } else {
                Text(name.take(2), color = Color.White)
            }
        }
        Text(name, style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .background(Surface2, CircleShape),
                    contentAlignment = Alignment.Center
                ) { Text(post.initials, color = Color.White) }
                Column(Modifier.padding(start = 10.dp)) {
                    Text(post.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Text(post.time, style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }

            if (post.hasImage) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Surface2)
                )
            }

            Text(
                post.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = "إعجاب", tint = TextMuted, modifier = Modifier.size(16.dp))
                    Text(" ${post.likes}", color = TextMuted, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "تعليق", tint = TextMuted, modifier = Modifier.size(16.dp))
                    Text(" ${post.comments}", color = TextMuted, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
