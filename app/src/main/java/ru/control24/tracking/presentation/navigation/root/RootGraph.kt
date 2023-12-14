package ru.control24.tracking.presentation.navigation.root

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.control24.tracking.presentation.MainViewModel
import ru.control24.tracking.presentation.navigation.auth.authNavGraph
import ru.control24.tracking.presentation.navigation.home.HomeScreen

@Composable
fun RootNavigationGraph(
    navHostController: NavHostController,
    startDestination: String,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navHostController,
        route = RootNavGraph.ROOT,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController = navHostController,
            uiEvent = viewModel::uiEvent,
            authState = viewModel::authState
        )
        composable(route = RootNavGraph.HOME) {
            HomeScreen(
                authState = viewModel::authState
            )
        }
    }
}

object RootNavGraph {
    const val ROOT = "root_graph"
    const val AUTH = "auth_graph"
    const val HOME = "home_graph"
}