package com.tommunyiri.dvtweatherapp.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.tommunyiri.dvtweatherapp.presentation.components.BottomNavigationBar
import com.tommunyiri.dvtweatherapp.presentation.navigation.MainNavigationScreens

/**
 * Composable function that represents the main screen of the application.
 *
 * @param navController The navigation controller used for handling navigation between screens.
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavHostController,
    onThemeUpdated: () -> Unit,
) {
    Scaffold(bottomBar = {
        BottomAppBar(containerColor = Color.Transparent) {
            BottomNavigationBar(
                navController = navController,
            )
        }
    }) {
        MainNavigationScreens(navController = navController, onThemeUpdated)
    }
}
