package ru.control24.tracking.core.navigation.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.control24.tracking.core.navigation.root.RootNavGraph
import ru.control24.tracking.core.presentation.ActiveUserState
import ru.control24.tracking.map.presentation.MapScreen
import ru.control24.tracking.monitoring.presentation.objects.ObjectsScreen
import ru.control24.tracking.UiEvent
import ru.control24.tracking.settings.presentation.settings.SettingsScreen

@Composable
fun HomeNavigationGraph(
    navController: NavHostController,
    rootNavController: NavController,
    paddingValues: PaddingValues,
    activeUserState: ActiveUserState,
    uiEvent: (UiEvent) -> Unit
) {
    NavHost(
        navController = navController,
        route = RootNavGraph.HOME,
        startDestination = Destinations.ObjectsScreen.route
    ) {
        composable(
            route = Destinations.ObjectsScreen.route,
            enterTransition = { fadeIn() + scaleIn(initialScale = .9f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 150)) + scaleOut(targetScale = .9f) }
        ) {
            ObjectsScreen(
                paddingValues = paddingValues,
                activeUserState = activeUserState
            )
        }
        composable(
            route = Destinations.MapScreen.route,
            enterTransition = { fadeIn() + scaleIn(initialScale = .9f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 150)) + scaleOut(targetScale = .9f) }
        ) {
            MapScreen(
                paddingValues = paddingValues,
                uiEvent = uiEvent
            )
        }
        composable(
            route = Destinations.SettingsScreen.route,
            enterTransition = { fadeIn() + scaleIn(initialScale = .9f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 150)) + scaleOut(targetScale = .9f) }
        ) {
            SettingsScreen(
                navigateToHelpScreen = { rootNavController.navigate(RootNavGraph.HELP) }
            )
        }
    }
}