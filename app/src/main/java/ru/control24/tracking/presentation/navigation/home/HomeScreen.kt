package ru.control24.tracking.presentation.navigation.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.control24.tracking.presentation.states.ObjectsState
import ru.control24.tracking.presentation.ui.components.BottomNavigationBar
import ru.control24.tracking.presentation.ui.components.bottomBarVisibility

@Composable
fun HomeScreen(
    rootNavController: NavController,
    objectsState: () -> ObjectsState
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
            objectsState = objectsState
        )
    }
}