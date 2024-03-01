package com.tommunyiri.dvtweatherapp.presentation.navigation.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings

sealed class NavItem {
    object Home :
        Item(path = NavPath.HOME.toString(), title = NavTitle.HOME, icon = Icons.Rounded.Home)

    object Search :
        Item(path = NavPath.SEARCH.toString(), title = NavTitle.SEARCH, icon = Icons.Rounded.Search)

    object Favorites :
        Item(path = NavPath.FAVORITE.toString(), title = NavTitle.FAVORITE, icon = Icons.Rounded.Favorite)

    object Settings :
        Item(
            path = NavPath.SETTINGS.toString(), title = NavTitle.SETTINGS, icon = Icons.Rounded.Settings)
}