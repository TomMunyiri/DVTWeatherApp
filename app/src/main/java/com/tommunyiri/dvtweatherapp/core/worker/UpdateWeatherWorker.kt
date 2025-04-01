package com.tommunyiri.dvtweatherapp.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tommunyiri.dvtweatherapp.core.utils.NotificationHelper
import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class UpdateWeatherWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val weatherUseCases: WeatherUseCases,
) : CoroutineWorker(context, params) {
    private val notificationHelper = NotificationHelper("Weather Update", context)
    private val sharedPrefs = SharedPreferenceHelper.getInstance(context)

    override suspend fun doWork(): Result {
        val location = sharedPrefs.getLocation() ?: return Result.failure()

        return try {
            // Get weather data
            val weatherResult = weatherUseCases.getWeather(location, true)
                .first()

            when (weatherResult) {
                is com.tommunyiri.dvtweatherapp.core.utils.Result.Success -> {
                    if (weatherResult.data != null) {
                        // Get forecast data
                        val forecastResult = weatherUseCases.getWeatherForecast(location, true)
                            .first()

                        when (forecastResult) {
                            is com.tommunyiri.dvtweatherapp.core.utils.Result.Success -> {
                                if (forecastResult.data != null) {
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
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
