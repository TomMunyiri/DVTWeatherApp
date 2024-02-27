package com.tommunyiri.dvtweatherapp.core

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.tommunyiri.dvtweatherapp.BuildConfig
import com.tommunyiri.dvtweatherapp.core.worker.MyWorkerFactory
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltAndroidApp
class DVTWeatherApplication() : Application(), Configuration.Provider {
    @Inject
    lateinit var weatherRepository: WeatherRepository
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(MyWorkerFactory(weatherRepository))
            .build()
}