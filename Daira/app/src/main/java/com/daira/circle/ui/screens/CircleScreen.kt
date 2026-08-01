package com.daira.circle.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daira.circle.data.firestore.FriendEntry
import com.daira.circle.ui.theme.BgDeep
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.Sage
import com.daira.circle.ui.theme.Surface
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted
import com.daira.circle.viewmodel.SocialViewModel
import kotlinx.coroutines.delay

@Composable
fun CircleScreen(viewModel: SocialViewModel) {
    val profile by viewModel.profile.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val joinFeedback by viewModel.joinFeedback.collectAsState()
    var joinCodeInput by remember { mutableStateOf("") }

    // تختفي رسالة النتيجة تلقائيًا بعد ٣ ثوانٍ
    LaunchedEffect(joinFeedback) {
        if (joinFeedback != null) {
            delay(3000)
            viewModel.clearJoinFeedback()
        }
    }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text("دائرتك", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                Text("مشتركة فعليًا بين الأصدقاء", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
        }

        // بطاقة رمز الدعوة الخاص بي
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Text("  رمز دعوتك الخاص", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            profile?.activeInviteCode?.takeIf { it.isNotBlank() } ?: "...",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Button(
                            onClick = { viewModel.regenerateInviteCode() },
                            colors = ButtonDefaults.buttonColors(containerColor = Peach, contentColor = BgDeep)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("  رمز جديد")
                        }
                    }
                    Text(
                        "شارك هذا الرمز مع من تثق به فقط. أي انضمام بالرمز القديم يتوقف فور توليد رمز جديد.",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }

        // بطاقة الانضمام برمز صديق
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("عندك رمز من صديق؟", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = joinCodeInput,
                            onValueChange = { joinCodeInput = it },
                            placeholder = { Text("مثال: 7X4-QRM") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Sage, focusedLabelColor = Sage, cursorColor = Sage
                            )
                        )
                        Button(
                            onClick = {
                                viewModel.joinByCode(joinCodeInput)
                                joinCodeInput = ""
                            },
                            modifier = Modifier.padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Sage, contentColor = BgDeep)
                        ) { Text("انضم") }
                    }
                    joinFeedback?.let {
                        Text(it, style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = Peach, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }

        item {
            Text(
                "الأعضاء · ${friends.size}",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(start = 20.dp, top = 6.dp, bottom = 6.dp)
            )
        }

        if (friends.isEmpty()) {
            item {
                Text(
                    "لسا ما عندك أصدقاء بالدائرة — شارك رمزك أو استخدم رمز صديق فوق",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }

        items(friends) { friend -> MemberRow(friend) }
    }
}

@Composable
private fun MemberRow(friend: FriendEntry) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(42.dp).background(Surface2, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(friend.initials, color = Color.White) }

        Column(Modifier.padding(start = 12.dp)) {
            Text(friend.displayName, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(friend.sinceLabel, style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}
