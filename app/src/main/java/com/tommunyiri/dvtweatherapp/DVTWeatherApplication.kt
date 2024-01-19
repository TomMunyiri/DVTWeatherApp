package com.tommunyiri.dvtweatherapp

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import com.tommunyiri.dvtweatherapp.utils.ThemeManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltAndroidApp
class DVTWeatherApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initTheme()
    }

    private fun initTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        runCatching {
            ThemeManager.applyTheme(requireNotNull(preferences.getString("theme_key", "")))
        }.onFailure { exception ->
            Timber.e("Theme Manager: $exception")
        }
    }

    override val workManagerConfiguration: Configuration
        get() = TODO("Not yet implemented")
}