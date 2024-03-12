package com.tommunyiri.dvtweatherapp.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tommunyiri.dvtweatherapp.core.utils.NotificationHelper
import com.tommunyiri.dvtweatherapp.core.utils.Result.Success
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

class UpdateWeatherWorker(
    context: Context,
    params: WorkerParameters,
    private val weatherUseCases: WeatherUseCases,
) : CoroutineWorker(context, params) {
    private val notificationHelper = NotificationHelper("Weather Update", context)
    private val sharedPrefs = SharedPreferenceHelper.getInstance(context)

    override suspend fun doWork(): Result {
        val location = sharedPrefs.getLocation()

        return when (val result = location?.let { weatherUseCases.getWeather.invoke(it, true) }) {
            is Success -> {
                if (result.data != null) {
                    when (
                        val foreResult =
                            location.let { weatherUseCases.getWeatherForecast.invoke(it, true) }
                    ) {
                        is Success -> {
                            if (foreResult.data != null) {
                                notificationHelper.createNotification()
                                Result.success()
                            } else {
                                Result.failure()
                            }
                        }

                        else -> Result.failure()
                    }
                } else {
                    Result.failure()
                }
            }

            else -> Result.failure()
        }
    }
}
