package com.tommunyiri.dvtweatherapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tommunyiri.dvtweatherapp.presentation.navigation.nav.NavItem
import com.tommunyiri.dvtweatherapp.presentation.screens.favorites.FavoritesScreen
import com.tommunyiri.dvtweatherapp.presentation.screens.home.HomeScreen
import com.tommunyiri.dvtweatherapp.presentation.screens.search.SearchScreen
import com.tommunyiri.dvtweatherapp.presentation.screens.settings.SettingsScreen

/**
 * Composable function that defines the navigation screens and their corresponding destinations.
 *
 * @param navController The navigation controller used for handling navigation between screens.
 */
@Composable
fun MainNavigationScreens(navController: NavHostController, onThemeUpdated: () -> Unit) {
    NavHost(
        navController,
        startDestination = NavItem.Home.path
    ) {
        composable(NavItem.Home.path) { HomeScreen() }
        composable(NavItem.Search.path) { SearchScreen(navController) }
        composable(NavItem.Favorites.path) { FavoritesScreen(navController) }
        composable(NavItem.Settings.path) { SettingsScreen(onThemeUpdated, navController) }
    }
}