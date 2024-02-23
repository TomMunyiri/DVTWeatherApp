package com.tommunyiri.dvtweatherapp.presentation.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.presentation.composables.ScreenTitle
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = hiltViewModel()) {
    val state by viewModel.settingsScreenState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitle(text = stringResource(id = R.string.settings))
        CacheDurationPreference(state)
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
        )
        ThemePreference(state)
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
        )
        TemperatureUnitPreference(state)
        val c = LocalContext.current.getString(R.string.temp_unit_celsius)
        val f = LocalContext.current.getString(R.string.temp_unit_fahrenheit)
        Button(onClick = { viewModel.setTemperatureUnit(c) }) {
            Text(text = "Click click bang")
        }
    }
}

@Composable
fun CacheDurationPreference(state: SettingsScreenState) {
    // Cache category
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(id = R.drawable.ic_cached),
                contentDescription = "Cache Icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(25.dp))
            Column {
                Text(
                    text = "Cache",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                state.cacheDuration?.let { cacheDuration ->
                    Text(
                        modifier = Modifier.padding(top = 7.dp),
                        text = "$cacheDuration ${stringResource(id = R.string.seconds)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ThemePreference(state: SettingsScreenState) {
    // Theme category
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
            Spacer(modifier = Modifier.width(25.dp))
            Column {
                Text(
                    text = "Theme",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                state.theme?.let { theme ->
                    Text(
                        modifier = Modifier.padding(top = 7.dp),
                        text = theme,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun TemperatureUnitPreference(state: SettingsScreenState) {
    // Unit category
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
            Spacer(modifier = Modifier.width(25.dp))
            Column {
                Text(
                    text = "Temperature Unit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                state.temperatureUnit?.let { temperatureUnit ->
                    Text(
                        modifier = Modifier.padding(top = 7.dp),
                        text = temperatureUnit,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
