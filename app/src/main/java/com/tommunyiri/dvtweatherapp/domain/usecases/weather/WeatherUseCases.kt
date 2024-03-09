package com.tommunyiri.dvtweatherapp.domain.usecases.weather

import com.tommunyiri.dvtweatherapp.domain.usecases.favorites.DeleteFavoriteLocationUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.favorites.GetFavoriteLocationsUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.favorites.SaveFavoriteLocationUseCase

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
data class WeatherUseCases(
    val getWeather: GetWeatherUseCase,
    val getWeatherForecast: GetWeatherForecastUseCase,
    val deleteWeatherData: DeleteWeatherDataUseCase,
    val deleteWeatherForecast: DeleteWeatherForecastUseCase,
    val getSearchWeather: GetSearchWeatherUseCase,
    val storeWeatherForecast: StoreWeatherForecastUseCase,
    val storeWeatherData: StoreWeatherDataUseCase,
    val saveFavoriteLocation: SaveFavoriteLocationUseCase,
    val deleteFavoriteLocation: DeleteFavoriteLocationUseCase,
    val getFavoriteLocations: GetFavoriteLocationsUseCase,
)
