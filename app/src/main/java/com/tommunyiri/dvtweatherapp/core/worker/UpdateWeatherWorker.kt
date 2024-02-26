package com.tommunyiri.dvtweatherapp.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.core.utils.NotificationHelper
import com.tommunyiri.dvtweatherapp.core.utils.Result.Success
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class UpdateWeatherWorker(context: Context, params: WorkerParameters, private val repository: WeatherRepository) : CoroutineWorker(context, params) {
    private val notificationHelper = NotificationHelper("Weather Update", context)
    private val sharedPrefs = SharedPreferenceHelper.getInstance(context)

    override suspend fun doWork(): Result {
        val location = sharedPrefs.getLocation()
        return when (val result = repository.getWeather(location, true)) {
            is Success -> {
                if (result.data != null) {
                    when (
                        val foreResult =
                            //repository.getForecastWeather(result.data.cityId, true)
                            repository.getForecastWeather(location, true)
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