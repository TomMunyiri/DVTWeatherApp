package com.tommunyiri.dvtweatherapp.presentation.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tommunyiri.dvtweatherapp.presentation.composables.SingleSelectDialog

@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = hiltViewModel()) {
    val state by viewModel.settingsScreenState.collectAsStateWithLifecycle()
    val showTempDialog = remember { mutableStateOf(false) }
    val showThemeDialog = remember { mutableStateOf(false) }
    val context= LocalContext.current
    if (showTempDialog.value) {
        val temperatureUnits =
            LocalContext.current.resources.getStringArray(R.array.unit_values_array).toList()
        val selectedTempUnit =
            if (state.temperatureUnit == LocalContext.current.getString(R.string.temp_unit_celsius)) 0 else 1
        SingleSelectDialog(
            optionsList = temperatureUnits,
            defaultSelected = selectedTempUnit,
            onCancelButtonClick = { showTempDialog.value = false },
            onDismissRequest = { showTempDialog.value = false },
            onItemSelected = {
                viewModel.saveTemperatureUnit(it)
            }
        )
    }
    if (showThemeDialog.value) {
        val themes =
            LocalContext.current.resources.getStringArray(R.array.theme_values_array).toList()
        val selectedTheme =
            when (state.theme) {
                LocalContext.current.getString(R.string.light_theme_value) -> 0
                LocalContext.current.getString(R.string.dark_theme_value) -> 1
                LocalContext.current.getString(R.string.auto_battery_value) -> 2
                else -> 3
            }
        SingleSelectDialog(
            optionsList = themes,
            defaultSelected = selectedTheme,
            onCancelButtonClick = { showThemeDialog.value = false },
            onDismissRequest = { showThemeDialog.value = false },
            onItemSelected = {
                viewModel.saveTheme(it,context)
            }
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitle(text = stringResource(id = R.string.settings))
        CacheDurationPreference(state)
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
        )
        ThemePreference(state, showThemeDialog)
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .width(1.dp)
        )
        TemperatureUnitPreference(state, showTempDialog)
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
fun ThemePreference(state: SettingsScreenState, showDialog: MutableState<Boolean>) {
    // Theme category
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable(
                onClick = { showDialog.value = true }
            )
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
fun TemperatureUnitPreference(state: SettingsScreenState, showDialog: MutableState<Boolean>) {
    // Unit category
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable(
                onClick = { showDialog.value = true }
            )
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
