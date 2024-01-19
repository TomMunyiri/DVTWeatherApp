package com.tommunyiri.dvtweatherapp.data.source.local

import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeatherForecast


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
interface WeatherLocalDataSource {
    suspend fun getWeather(): DBWeather?

    suspend fun saveWeather(weather: DBWeather)

    suspend fun deleteWeather()

    suspend fun getForecastWeather(): List<DBWeatherForecast>?

    suspend fun saveForecastWeather(weatherForecast: DBWeatherForecast)

    suspend fun deleteForecastWeather()
}