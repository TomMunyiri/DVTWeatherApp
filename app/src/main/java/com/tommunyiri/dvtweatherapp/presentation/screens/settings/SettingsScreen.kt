package com.tommunyiri.dvtweatherapp.presentation.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.presentation.composables.ScreenTitle

/**
 * Composable function that represents the profile screen of the application.
 */
@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitle(text = stringResource(id = R.string.settings))
        CacheDurationPreference()
    }
}

@Composable
fun CacheDurationPreference() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Cache category
        Card(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_cached), // Assuming your icon resource
                        contentDescription = "Cache Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Cache",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                /*NumberPicker(
                    value = remember { mutableFloatStateOf(600f) }, // Change type based on actual value
                    modifier = Modifier.fillMaxWidth(),
                    range = 0f..Float.MAX_VALUE, // Adjust range as needed
                    onValueChange = { newValue -> }, // Handle value change
                    label = stringResource(id = R.string.cache_string),
                    enabled = true,
                    readOnly = false,
                    steps = 1f // Adjust step size as needed
                )*/
            }
        }

        // Theme category
        Card(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_palette),
                        contentDescription = "Theme Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Theme",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { },
                    content = {
                        // Populate content with items from your string arrays
                        // Use DropDownMenuItem and Text for each item
                    },
                    /*label = {
                        Text(
                            text = stringResource(id = R.string.dark_theme_value),
                            fontSize = 16.sp
                        )
                    }*/
                )
            }
        }

        // Unit category
        Card(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_baseline_title_24),
                        contentDescription = "Unit Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Temperature Unit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { },
                    content = {
                        // Populate content with items from your string arrays
                        // Use DropDownMenuItem and Text for each item
                    },
                    /*label = {
                        Text(
                            text = stringResource(id = R.string.preference_temperature_unit_title),
                            fontSize = 16.sp
                        )
                    }*/
                )
            }
        }
    }
}
