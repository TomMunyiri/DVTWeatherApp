package com.tommunyiri.dvtweatherapp.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
object ThemeManager {
    private const val LIGHT_MODE = "Light"
    private const val DARK_MODE = "Dark"
    private const val AUTO_BATTERY_MODE = "Auto-battery"
    private const val FOLLOW_SYSTEM_MODE = "System"

    /**
     * This function helps persist the theme set by the user by getting the [themePreference] on initial startup
     * of the application.
     */
    fun applyTheme(themePreference: String) {
        when (themePreference) {
            /*LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AUTO_BATTERY_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            FOLLOW_SYSTEM_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)*/

            /*LIGHT_MODE -> MaterialTheme.colo = colors.light
            DARK_MODE -> MaterialTheme.colors = colors.dark
            else -> MaterialTheme.colors = colors.system*/
        }
    }
}