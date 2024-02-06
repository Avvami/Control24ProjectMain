package ru.control24.tracking.presentation.navigation.root

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.control24.tracking.presentation.MainViewModel
import ru.control24.tracking.presentation.navigation.auth.authNavGraph
import ru.control24.tracking.presentation.navigation.home.HomeScreen
import ru.control24.tracking.presentation.ui.screens.help.HelpScreen

@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    startDestination: String,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        route = RootNavGraph.ROOT,
        startDestination = startDestination
    ) {
        authNavGraph(
            rootNavController = navController,
            uiEvent = mainViewModel::uiEvent,
            objectsState = mainViewModel::objectsState
        )
        composable(route = RootNavGraph.HOME) {
            HomeScreen(
                rootNavController = navController,
                objectsState = mainViewModel::objectsState
            )
        }
        composable(
            route = RootNavGraph.HELP,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(350, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    animationSpec = tween(350, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            HelpScreen(
                popBackStack = { if (navController.canGoBack) navController.popBackStack() }
            )
        }
    }
}

object RootNavGraph {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val HOME = "home_graph"

    const val HELP = "help_screen"
}

val NavHostController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED