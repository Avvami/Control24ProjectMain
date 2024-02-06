package ru.control24.tracking.presentation.navigation.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.control24.tracking.R

sealed class Destinations(
    val route: String,
    @StringRes
    val title: Int? = null,
    @DrawableRes
    val selectedIcon: Int? = null,
    @DrawableRes
    val unselectedIcon: Int? = null
) {
    data object ObjectsScreen: Destinations(
        route = "objects_screen",
        title = R.string.objects,
        selectedIcon = R.drawable.icon_format_list_bulleted_fill0,
        unselectedIcon = R.drawable.icon_format_list_bulleted_fill0
    )

    data object MapScreen: Destinations(
        route = "map_screen",
        title = R.string.map,
        selectedIcon = R.drawable.icon_map_fill1,
        unselectedIcon = R.drawable.icon_map_fill0
    )

    data object SettingsScreen: Destinations(
        route = "settings_screen",
        title = R.string.settings,
        selectedIcon = R.drawable.icon_settings_fill1,
        unselectedIcon = R.drawable.icon_settings_fill0
    )

    data object ThemeScreen: Destinations(route = "theme_screen")
    data object MapConfigScreen: Destinations(route = "map_config_screen")
    data object ObjectsItemAppearanceScreen: Destinations(route = "objects_item_appearance_screen")
    data object AboutAppScreen: Destinations(route = "about_app_screen")
    data object HelpScreen: Destinations(route = "help_screen")
}