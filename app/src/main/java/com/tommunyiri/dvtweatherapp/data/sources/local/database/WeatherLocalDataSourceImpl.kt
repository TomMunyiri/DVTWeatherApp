package com.tommunyiri.dvtweatherapp.data.sources.local.database

import com.tommunyiri.dvtweatherapp.core.di.scope.IoDispatcher
import com.tommunyiri.dvtweatherapp.data.sources.local.database.dao.WeatherDao
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBFavoriteLocation
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeatherForecast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherLocalDataSourceImpl
@Inject
constructor(
    private val weatherDao: WeatherDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WeatherLocalDataSource {
    override suspend fun getWeather(): DBWeather =
        withContext(ioDispatcher) {
            return@withContext weatherDao.getWeather()
        }

    override suspend fun saveWeather(weather: DBWeather) =
        withContext(ioDispatcher) {
            weatherDao.insertWeather(weather)
        }

    override suspend fun deleteWeather() =
        withContext(ioDispatcher) {
            weatherDao.deleteAllWeather()
        }

    override suspend fun getForecastWeather(): List<DBWeatherForecast>? =
        withContext(ioDispatcher) {
            return@withContext weatherDao.getAllWeatherForecast()
        }

    override suspend fun saveForecastWeather(weatherForecast: DBWeatherForecast) =
        withContext(ioDispatcher) {
            weatherDao.insertForecastWeather(weatherForecast)
        }

    override suspend fun deleteForecastWeather() =
        withContext(ioDispatcher) {
            weatherDao.deleteAllWeatherForecast()
        }

    override suspend fun saveFavoriteLocation(favoriteLocation: DBFavoriteLocation): List<Long> =
        withContext(ioDispatcher) {
            return@withContext weatherDao.insertFavoriteCity(favoriteLocation)
        }

    override fun getFavoriteLocations(): Flow<List<DBFavoriteLocation>> =
        weatherDao.getAllFavoriteLocations()

    override suspend fun deleteFavoriteLocation(name: String): Int =
        withContext(ioDispatcher) {
            return@withContext weatherDao.deleteFavoriteLocation(name)
        }
}
