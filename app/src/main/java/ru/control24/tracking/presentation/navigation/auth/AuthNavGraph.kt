package ru.control24.tracking.presentation.navigation.auth

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.control24.tracking.presentation.UiEvent
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.ObjectsState
import ru.control24.tracking.presentation.ui.screens.auth.AuthScreen

fun NavGraphBuilder.authNavGraph(
    rootNavController: NavController,
    uiEvent: (UiEvent) -> Unit,
    objectsState: () -> ObjectsState
) {
    navigation(
        route = RootNavGraph.AUTH,
        startDestination = AuthScreen.Auth.route
    ) {
        composable(
            route = AuthScreen.Auth.route,
            enterTransition = { fadeIn() + scaleIn(initialScale = .9f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 150)) + scaleOut(targetScale = .9f) }
        ) {
            AuthScreen(
                uiEvent = uiEvent,
                objectsState = objectsState,
                navigateToHelpScreen = { rootNavController.navigate(RootNavGraph.HELP) }
            )
        }
    }
}

sealed class AuthScreen(val route: String) {
    data object Auth: AuthScreen("auth_screen")
}