package com.tommunyiri.dvtweatherapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.material.search.SearchBar
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.ui.theme.md_theme_dark_secondaryContainer

/**
 * Composable function that represents the search screen of the application.
 */
@Composable
fun SearchScreen() {
    Column(modifier = Modifier.padding(5.dp, 45.dp, 5.dp, 5.dp)) {
        OutlinedTextField(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            },
            value = "",
            onValueChange = {

            },
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.enter_city_text))
            },
            maxLines = 1,
            singleLine = true
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.search),
                style = typography.titleLarge,
                color = md_theme_dark_secondaryContainer
            )
        }
    }
}