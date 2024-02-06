package ru.control24.tracking.presentation.navigation.home

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
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.ObjectsState
import ru.control24.tracking.presentation.ui.screens.map.MapScreen
import ru.control24.tracking.presentation.ui.screens.objects.ObjectsScreen
import ru.control24.tracking.presentation.ui.screens.settings.SettingsScreen

@Composable
fun HomeNavigationGraph(
    navController: NavHostController,
    rootNavController: NavController,
    paddingValues: PaddingValues,
    objectsState: () -> ObjectsState
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
                objectsState = objectsState
            )
        }
        composable(
            route = Destinations.MapScreen.route,
            enterTransition = { fadeIn() + scaleIn(initialScale = .9f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 150)) + scaleOut(targetScale = .9f) }
        ) {
            MapScreen()
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