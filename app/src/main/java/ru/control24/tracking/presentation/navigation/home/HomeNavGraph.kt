package ru.control24.tracking.presentation.navigation.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.AuthState
import ru.control24.tracking.presentation.ui.screens.help.HelpScreen
import ru.control24.tracking.presentation.ui.screens.map.MapScreen
import ru.control24.tracking.presentation.ui.screens.objects.ObjectsScreen
import ru.control24.tracking.presentation.ui.screens.settings.SettingsScreen

@Composable
fun HomeGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    authState: () -> AuthState
) {
    NavHost(
        navController = navController,
        route = RootNavGraph.HOME,
        startDestination = Destinations.ObjectsScreen.route
    ) {
        composable(route = Destinations.ObjectsScreen.route) {
            ObjectsScreen(
                navController = navController,
                authState = authState
            )
        }
        composable(route = Destinations.MapScreen.route) {
            MapScreen(navController = navController)
        }
        composable(route = Destinations.SettingsScreen.route) {
            SettingsScreen(navController = navController)
        }
        composable(route = Destinations.HelpScreen.route) {
            HelpScreen { navController.popBackStack() }
        }
    }
}