package com.daira.circle.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.daira.circle.ui.theme.BgDeep2
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.TextMuted

sealed class Tab(val route: String, val label: String) {
    data object Home : Tab("home", "الرئيسية")
    data object Chat : Tab("chat", "الدردشة")
    data object Circle : Tab("circle", "دائرتك")
    data object Profile : Tab("profile", "حسابك")
}

val tabs = listOf(Tab.Home, Tab.Chat, Tab.Circle, Tab.Profile)

@Composable
fun DairaBottomNav(currentRoute: String, onTabSelected: (Tab) -> Unit) {
    NavigationBar(containerColor = BgDeep2) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = when (tab) {
                            Tab.Home -> Icons.Filled.Home
                            Tab.Chat -> Icons.Filled.Chat
                            Tab.Circle -> Icons.Filled.Groups
                            Tab.Profile -> Icons.Filled.Person
                        },
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Peach,
                    selectedTextColor = Peach,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = BgDeep2,
                )
            )
        }
    }
}
