package com.tommunyiri.dvtweatherapp.data.sources.remote

import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeather
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherForecast
import com.tommunyiri.dvtweatherapp.core.utils.Result


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
interface WeatherRemoteDataSource {
    suspend fun getWeather(location: LocationModel): Result<NetworkWeather>

    //suspend fun getWeatherForecast(cityId: Int): Result<List<NetworkWeatherForecast>>
    suspend fun getWeatherForecast(location: LocationModel): Result<List<NetworkWeatherForecast>>

    suspend fun getSearchWeather(query: String): Result<NetworkWeather>
}