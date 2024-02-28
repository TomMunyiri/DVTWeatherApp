package com.tommunyiri.dvtweatherapp.domain.usecases.weather

import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository


/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class DeleteWeatherForecastUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke() {
        weatherRepository.deleteForecastData()
    }
}