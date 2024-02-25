package com.tommunyiri.dvtweatherapp.presentation.screens.home

import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast


/**
 * Created by Tom Munyiri on 19/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
data class HomeScreenState(
    val weatherForecastList: List<WeatherForecast>? = null,
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val isLoadingForecast: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
