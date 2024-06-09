package ru.control24.tracking.core.presentation.components.navbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.control24.tracking.core.navigation.home.Destinations

@Composable
fun bottomBarVisibility(
    navController: NavController,
): MutableState<Boolean> {

    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    when (navBackStackEntry?.destination?.route) {
        Destinations.ObjectsScreen.route -> bottomBarState.value = true
        Destinations.MapScreen.route -> bottomBarState.value = true
        Destinations.SettingsScreen.route -> bottomBarState.value = true
        else -> bottomBarState.value = false
    }

    return bottomBarState
}