package com.tommunyiri.dvtweatherapp.domain.repository

import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.core.utils.Result

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
interface WeatherRepository {
    suspend fun getWeather(location: LocationModel, refresh: Boolean): Result<Weather?>

    //suspend fun getForecastWeather(cityId: Int, refresh: Boolean): Result<List<WeatherForecast>?>
    suspend fun getForecastWeather(
        location: LocationModel,
        refresh: Boolean
    ): Result<List<WeatherForecast>?>

    suspend fun getSearchWeather(location: String): Result<Weather?>

    suspend fun storeWeatherData(weather: Weather)

    suspend fun storeForecastData(forecasts: List<WeatherForecast>)

    suspend fun deleteWeatherData()

    suspend fun deleteForecastData()

    suspend fun storeFavoriteLocationData(favoriteLocation: FavoriteLocation)

    suspend fun getFavoriteLocations(): Result<List<FavoriteLocation>?>

    suspend fun deleteFavoriteLocation(name:String): Result<Int>?
}