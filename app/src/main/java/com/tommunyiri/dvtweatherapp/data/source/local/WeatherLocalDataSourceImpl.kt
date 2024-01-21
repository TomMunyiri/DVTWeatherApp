package com.tommunyiri.dvtweatherapp.data.source.local

import com.tommunyiri.dvtweatherapp.data.source.local.dao.WeatherDao
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBFavoriteLocation
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeatherForecast
import com.tommunyiri.dvtweatherapp.di.scope.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherLocalDataSourceImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WeatherLocalDataSource {
    override suspend fun getWeather(): DBWeather = withContext(ioDispatcher) {
        return@withContext weatherDao.getWeather()
    }

    override suspend fun saveWeather(weather: DBWeather) = withContext(ioDispatcher) {
        weatherDao.insertWeather(weather)
    }

    override suspend fun deleteWeather() = withContext(ioDispatcher) {
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

    override suspend fun deleteForecastWeather() = withContext(ioDispatcher) {
        weatherDao.deleteAllWeatherForecast()
    }

    override suspend fun saveFavoriteLocation(favoriteLocation: DBFavoriteLocation) =
        withContext(ioDispatcher) {
            weatherDao.insertFavoriteCity(favoriteLocation)
        }
}