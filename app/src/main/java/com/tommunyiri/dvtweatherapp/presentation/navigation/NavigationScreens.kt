package com.tommunyiri.dvtweatherapp.presentation.navigation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tommunyiri.dvtweatherapp.presentation.navigation.nav.NavItem
import com.tommunyiri.dvtweatherapp.presentation.screens.FavoritesScreen
import com.tommunyiri.dvtweatherapp.presentation.screens.home.HomeScreen
import com.tommunyiri.dvtweatherapp.presentation.screens.SearchScreen
import com.tommunyiri.dvtweatherapp.presentation.screens.SettingsScreen

/**
 * Composable function that defines the navigation screens and their corresponding destinations.
 *
 * @param navController The navigation controller used for handling navigation between screens.
 */
@Composable
fun NavigationScreens(navController: NavHostController) {
    NavHost(
        navController,
        startDestination = NavItem.Home.path
    ) {
        composable(NavItem.Home.path) { HomeScreen() }
        composable(NavItem.Search.path) { SearchScreen() }
        composable(NavItem.Favorites.path) { FavoritesScreen() }
        composable(NavItem.Settings.path) { SettingsScreen() }
    }
}