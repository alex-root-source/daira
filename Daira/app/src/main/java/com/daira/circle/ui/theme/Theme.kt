package com.daira.circle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DairaColorScheme = darkColorScheme(
    background = BgDeep,
    surface = Surface,
    primary = Peach,
    secondary = Sage,
    onBackground = TextLight,
    onSurface = TextLight,
    onPrimary = Color(0xFF241A16),
)

@Composable
fun DairaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DairaColorScheme,
        typography = DairaTypography,
        content = content
    )
}
