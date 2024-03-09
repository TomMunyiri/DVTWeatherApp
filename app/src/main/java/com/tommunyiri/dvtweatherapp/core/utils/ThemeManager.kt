package com.tommunyiri.dvtweatherapp.core.utils

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
object ThemeManager {
    private const val LIGHT_MODE = "Light"

    /**
     * This function helps persist the theme set by the user by getting the [themePreference] on initial startup
     * of the application.
     */

    fun getTheme(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val savedTheme = prefs.getString("theme_key", "Light")
        return savedTheme != LIGHT_MODE
    }
}
