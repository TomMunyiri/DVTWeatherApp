package com.tommunyiri.dvtweatherapp.presentation.screens.settings

import androidx.lifecycle.ViewModel
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.usecases.preferences.GetSharedPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 22/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@HiltViewModel
class SettingsScreenViewModel
    @Inject
    constructor(private val getPrefsUseCase: GetSharedPreferencesUseCase) :
    ViewModel() {
        private val _settingsScreenState = MutableStateFlow(SettingsScreenState())
        val settingsScreenState: StateFlow<SettingsScreenState> = _settingsScreenState.asStateFlow()
        val prefs = getSharedPrefs()

        init {
            getSettings()
        }

        private fun getSettings() {
            _settingsScreenState.update { currentState ->
                currentState.copy(
                    temperatureUnit = prefs.getSelectedTemperatureUnit(),
                    cacheDuration = prefs.getUserSetCacheDuration(),
                    theme = prefs.getSelectedThemePref(),
                )
            }
        }

        fun saveTemperatureUnit(temperatureUnit: String) {
            _settingsScreenState.update { currentState ->
                currentState.copy(
                    temperatureUnit = temperatureUnit,
                )
            }
            prefs.saveTemperatureUnit(temperatureUnit)
        }

        fun saveCacheDurationPref(cacheDuration: String) {
            _settingsScreenState.update { currentState ->
                currentState.copy(
                    cacheDuration = cacheDuration,
                )
            }
            prefs.saveCacheDuration(cacheDuration)
        }

        fun saveTheme(theme: String) {
            _settingsScreenState.update { currentState ->
                currentState.copy(
                    theme = theme,
                )
            }
            prefs.saveThemePref(theme)
        }

        private fun getSharedPrefs(): SharedPreferenceHelper {
            return getPrefsUseCase.invoke()
        }
    }
