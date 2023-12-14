package ru.control24.tracking.presentation.navigation.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ru.control24.tracking.presentation.states.AuthState
import ru.control24.tracking.presentation.ui.components.BottomNavigationBar
import ru.control24.tracking.presentation.ui.components.bottomBarVisibility

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController(),
    authState: () -> AuthState
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                state = bottomBarVisibility(navController)
            )
        }
    ) { paddingValues ->
        HomeGraph(
            navController = navController,
            paddingValues = paddingValues,
            authState = authState
        )
    }
}