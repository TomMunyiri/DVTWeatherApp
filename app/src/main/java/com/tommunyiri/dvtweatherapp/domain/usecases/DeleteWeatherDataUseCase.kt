package com.tommunyiri.dvtweatherapp.domain.usecases

import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository


/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class DeleteWeatherDataUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(){
        weatherRepository.deleteWeatherData()
    }
}