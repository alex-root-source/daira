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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daira.circle.data.db.MemberEntity
import com.daira.circle.ui.theme.BgDeep
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.Surface
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted
import com.daira.circle.viewmodel.DairaViewModel

@Composable
fun CircleScreen(viewModel: DairaViewModel) {
    val activeCode by viewModel.activeInviteCode.collectAsState()
    val members by viewModel.members.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text("دائرتك", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Text("إدارة الأعضاء والدعوات", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = TextMuted)
        }

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
                    Text(
                        "  رمز دعوتك الخاص",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // يُقرأ مباشرة من قاعدة البيانات (SQLite) — يبقى محفوظًا بعد إغلاق التطبيق
                    Text(
                        activeCode?.code ?: "...",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Button(
                        onClick = { viewModel.regenerateInviteCode() }, // يبطّل القديم ويولّد رمزًا جديدًا في نفس العملية
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

        Text(
            "الأعضاء · ${members.size}",
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 6.dp)
        )

        LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp)) {
            items(members) { member -> MemberRow(member) }
        }
    }
}

@Composable
private fun MemberRow(member: MemberEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(42.dp)
                .background(Surface2, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(member.initials, color = Color.White) }

        Column(Modifier.padding(start = 12.dp)) {
            Text(member.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Text(member.sinceLabel, style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}
