package com.tommunyiri.dvtweatherapp.data.source.remote

import com.tommunyiri.dvtweatherapp.data.model.LocationModel
import com.tommunyiri.dvtweatherapp.data.model.NetworkWeather
import com.tommunyiri.dvtweatherapp.data.model.NetworkWeatherForecast
import com.tommunyiri.dvtweatherapp.utils.Result


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
interface WeatherRemoteDataSource {
    suspend fun getWeather(location: LocationModel): Result<NetworkWeather>

    suspend fun getWeatherForecast(cityId: Int): Result<List<NetworkWeatherForecast>>

    suspend fun getSearchWeather(query: String): Result<NetworkWeather>
}