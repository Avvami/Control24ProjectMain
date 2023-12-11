package ru.control24.tracking.presentation.navigation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.control24.tracking.presentation.UIEvent
import ru.control24.tracking.presentation.navigation.root.RootNavGraph
import ru.control24.tracking.presentation.states.AuthState
import ru.control24.tracking.presentation.ui.screens.help.HelpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    uiEvent: (UIEvent) -> Unit,
    authState: AuthState
) {
    navigation(
        route = RootNavGraph.AUTH,
        startDestination = AuthScreen.Auth.route
    ) {
        composable(route = AuthScreen.Auth.route) {
            ru.control24.tracking.presentation.ui.screens.auth.AuthScreen(
                uiEvent = uiEvent,
                authState = authState,
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