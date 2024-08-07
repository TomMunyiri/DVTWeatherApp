package com.tommunyiri.dvtweatherapp.presentation.screens.settings

/**
 * Created by Tom Munyiri on 23/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

data class SettingsScreenState(
    val theme: String? = null,
    val temperatureUnit: String? = null,
    val cacheDuration: String? = null,
)
