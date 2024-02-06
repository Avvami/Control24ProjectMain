package ru.control24.tracking.presentation.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.control24.tracking.presentation.UiEvent
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.ObjectsState
import ru.control24.tracking.presentation.ui.screens.auth.AuthScreen
import ru.control24.tracking.presentation.ui.screens.help.HelpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    uiEvent: (UiEvent) -> Unit,
    objectsState: () -> ObjectsState
) {
    navigation(
        route = RootNavGraph.AUTH,
        startDestination = AuthScreen.Auth.route
    ) {
        composable(route = AuthScreen.Auth.route) {
            AuthScreen(
                uiEvent = uiEvent,
                objectsState = objectsState,
                navigateToHelpScreen = { navController.navigate(AuthScreen.Help.route) }
            )
        }
        composable(route = AuthScreen.Help.route) {
            HelpScreen(
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}

sealed class AuthScreen(val route: String) {
    data object Auth: AuthScreen("auth_screen")
    data object Help: AuthScreen("help_screen")
}