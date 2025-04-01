package com.tommunyiri.dvtweatherapp.domain.repository

import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
interface WeatherRepository {
    suspend fun getWeather(
        location: LocationModel,
        refresh: Boolean,
    ): Flow<Result<Weather?>>

    // suspend fun getForecastWeather(cityId: Int, refresh: Boolean): Result<List<WeatherForecast>?>
    suspend fun getForecastWeather(
        location: LocationModel,
        refresh: Boolean,
    ): Flow<Result<List<WeatherForecast>?>>

    suspend fun getSearchWeather(location: String): Flow<Result<Weather?>>

    suspend fun storeWeatherData(weather: Weather)

    suspend fun storeForecastData(forecasts: List<WeatherForecast>)

    suspend fun deleteWeatherData()

    suspend fun deleteForecastData()

    suspend fun storeFavoriteLocationData(favoriteLocation: FavoriteLocation): Flow<Result<List<Long>>>

    suspend fun getFavoriteLocations(): Flow<Result<List<FavoriteLocation>?>>

    suspend fun deleteFavoriteLocation(name: String): Flow<Result<Int>>
}
