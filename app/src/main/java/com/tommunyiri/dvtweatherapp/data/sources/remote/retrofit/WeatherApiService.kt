package com.tommunyiri.dvtweatherapp.data.sources.remote.retrofit

import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeather
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
interface WeatherApiService {
    /**
     * This function gets the [NetworkWeather] for the [location] the
     * user searched for.
     */
    @GET("/data/2.5/weather")
    suspend fun getSpecificWeather(
        @Query("q") location: String,
    ): Response<NetworkWeather>

    // This function gets the weather information for the user's location.
    @GET("/data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<NetworkWeather>

    // This function gets the weather forecast information for the user's location.
    @GET("data/2.5/forecast")
    suspend fun getWeatherForecast(
        // @Query("id") cityId: Int
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): Response<NetworkWeatherForecastResponse>
}
