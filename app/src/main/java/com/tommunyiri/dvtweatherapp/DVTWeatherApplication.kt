package com.tommunyiri.dvtweatherapp

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.tommunyiri.dvtweatherapp.data.source.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.ThemeManager
import com.tommunyiri.dvtweatherapp.worker.MyWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltAndroidApp
class DVTWeatherApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var weatherRepository: WeatherRepository
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

    override fun getWorkManagerConfiguration(): Configuration {
        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(MyWorkerFactory(weatherRepository))
        // Add here other factories that you may need in this application

        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(myWorkerFactory)
            .build()
    }
}