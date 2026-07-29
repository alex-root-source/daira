package com.daira.circle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daira.circle.ui.components.DairaBottomNav
import com.daira.circle.ui.components.Tab
import com.daira.circle.ui.screens.ChatScreen
import com.daira.circle.ui.screens.CircleScreen
import com.daira.circle.ui.screens.HomeScreen
import com.daira.circle.ui.screens.ProfileScreen
import com.daira.circle.ui.theme.DairaTheme
import com.daira.circle.viewmodel.DairaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DairaTheme {
                Surface {
                    DairaApp()
                }
            }
        }
    }
}

@Composable
fun DairaApp() {
    var currentTab by remember { mutableStateOf<Tab>(Tab.Home) }
    val viewModel: DairaViewModel = viewModel()

    Scaffold(
        bottomBar = {
            DairaBottomNav(currentRoute = currentTab.route) { tab -> currentTab = tab }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(padding)) {
            when (currentTab) {
                Tab.Home -> HomeScreen()
                Tab.Chat -> ChatScreen(viewModel)
                Tab.Circle -> CircleScreen(viewModel)
                Tab.Profile -> ProfileScreen()
            }
        }
    }
}
