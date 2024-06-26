package ru.control24.tracking.core.navigation.root

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.control24.tracking.MainViewModel
import ru.control24.tracking.auth.presentation.login.AuthScreen
import ru.control24.tracking.core.navigation.home.HomeScreen
import ru.control24.tracking.settings.presentation.help.HelpScreen

@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        route = RootNavGraph.ROOT,
        startDestination = mainViewModel.startDestination
    ) {
        composable(
            route = RootNavGraph.AUTH,
            enterTransition = { fadeIn() + scaleIn(initialScale = .9f) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 150)) + scaleOut(targetScale = .9f) }
        ) {
            AuthScreen(
                uiEvent = mainViewModel::uiEvent,
                activeUserState = mainViewModel.activeUserState.collectAsStateWithLifecycle(),
                navigateToHelpScreen = { navController.navigate(RootNavGraph.HELP) }
            )
        }
        composable(route = RootNavGraph.HOME) {
            HomeScreen(
                rootNavController = navController,
                activeUserState = mainViewModel.activeUserState.collectAsStateWithLifecycle(),
                uiEvent = mainViewModel::uiEvent
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
    const val HOME = "home_graph"

    const val AUTH = "auth_screen"
    const val HELP = "help_screen"
}

val NavHostController.canGoBack: Boolean
    get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED