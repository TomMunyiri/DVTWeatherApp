package com.tommunyiri.dvtweatherapp.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.tommunyiri.dvtweatherapp.presentation.composables.BottomNavigationBar
import com.tommunyiri.dvtweatherapp.presentation.navigation.NavigationScreens
import com.tommunyiri.dvtweatherapp.ui.theme.sunny

/**
 * Composable function that represents the main screen of the application.
 *
 * @param navController The navigation controller used for handling navigation between screens.
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(bottomBar = {
        BottomAppBar(containerColor = Color.Transparent) {
            BottomNavigationBar(
                navController = navController
            )
        }
    }) {
        NavigationScreens(navController = navController)
    }
}