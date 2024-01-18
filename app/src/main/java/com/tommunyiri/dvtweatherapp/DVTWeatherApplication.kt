package com.tommunyiri.dvtweatherapp

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp


/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltAndroidApp
class DVTWeatherApplication: Application(), Configuration.Provider {
    override val workManagerConfiguration: Configuration
        get() = TODO("Not yet implemented")
}