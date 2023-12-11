package ru.control24.tracking.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.control24.tracking.presentation.navigation.home.Destinations

@Composable
fun SettingsScreen(
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TextButton(onClick = { navController.navigate(Destinations.HelpScreen.route) }) {
            Text(text = "Settings screen\nNavigate to help")
        }
    }
}