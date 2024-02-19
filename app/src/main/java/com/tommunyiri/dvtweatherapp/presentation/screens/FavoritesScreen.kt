package com.tommunyiri.dvtweatherapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.ui.theme.md_theme_light_inversePrimary

/**
 * Composable function that represents the list screen of the application.
 */
@Composable
fun FavoritesScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.favorite),
            style = typography.titleLarge,
            color = md_theme_light_inversePrimary
        )
    }
}