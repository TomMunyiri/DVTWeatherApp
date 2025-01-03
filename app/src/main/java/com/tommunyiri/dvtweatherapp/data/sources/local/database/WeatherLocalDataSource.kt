package com.tommunyiri.dvtweatherapp.data.sources.local.database

import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBFavoriteLocation
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeatherForecast
import kotlinx.coroutines.flow.Flow

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

    suspend fun saveFavoriteLocation(favoriteLocation: DBFavoriteLocation): List<Long>

    fun getFavoriteLocations(): Flow<List<DBFavoriteLocation>?>

    suspend fun deleteFavoriteLocation(name: String): Int
}
