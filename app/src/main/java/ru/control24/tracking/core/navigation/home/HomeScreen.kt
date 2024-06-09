package ru.control24.tracking.core.navigation.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.control24.tracking.UiEvent
import ru.control24.tracking.core.presentation.ActiveUserState
import ru.control24.tracking.core.presentation.components.navbar.BottomNavigationBar
import ru.control24.tracking.core.presentation.components.navbar.bottomBarVisibility

@Composable
fun HomeScreen(
    rootNavController: NavController,
    activeUserState: ActiveUserState,
    uiEvent: (UiEvent) -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, state = bottomBarVisibility(navController))
        }
    ) { paddingValues ->
        HomeNavigationGraph(
            navController = navController,
            rootNavController = rootNavController,
            paddingValues = paddingValues,
            activeUserState = activeUserState,
            uiEvent = uiEvent
        )
    }
}