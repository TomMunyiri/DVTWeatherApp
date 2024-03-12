package com.tommunyiri.dvtweatherapp.core.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class MyWorkerFactory(private val weatherUseCases: WeatherUseCases) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            UpdateWeatherWorker::class.java.name -> {
                UpdateWeatherWorker(appContext, workerParameters, weatherUseCases)
            }

            else -> {
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
            }
        }
    }
}
