package com.tommunyiri.dvtweatherapp.data.source.repository

import com.tommunyiri.dvtweatherapp.data.model.City
import com.tommunyiri.dvtweatherapp.data.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.data.model.LocationModel
import com.tommunyiri.dvtweatherapp.data.model.Weather
import com.tommunyiri.dvtweatherapp.data.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBFavoriteLocation
import com.tommunyiri.dvtweatherapp.utils.Result

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