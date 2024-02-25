package com.tommunyiri.dvtweatherapp.domain.usecases

import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.Result

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetWeatherUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(location: LocationModel, refresh: Boolean): Result<Weather?> {
        return weatherRepository.getWeather(location, refresh)
    }
}