package com.daira.circle.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .padding(top = 24.dp)
                .size(82.dp)
                .border(2.dp, Peach, CircleShape)
                .padding(4.dp)
                .background(Surface2, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text("أنت", color = Color.White) }

        Text("حسابك", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 10.dp))
        Text("عضو في الدائرة منذ فبراير ٢٠٢٤", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)

        Row(
            modifier = Modifier.padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            StatItem("٦", "أصدقاء")
            StatItem("٢٤", "منشور")
            StatItem("٣", "قصص نشطة")
        }

        HorizontalDivider(color = Surface2)
        SettingRow("الخصوصية والدعوات")
        SettingRow("الإشعارات")
        SettingRow("النسخ الاحتياطي المحلي")
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Text(label, style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun SettingRow(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        Text("‹", color = TextMuted)
    }
    HorizontalDivider(color = Surface2)
}
