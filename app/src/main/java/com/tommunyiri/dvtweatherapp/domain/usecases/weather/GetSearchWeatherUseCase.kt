package com.tommunyiri.dvtweatherapp.domain.usecases.weather

import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.domain.utils.convertKelvinToCelsius

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetSearchWeatherUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(location: String): Result<Weather?> {
        val result = weatherRepository.getSearchWeather(location)
        when (result) {
            is Result.Success -> {
                if (result.data != null) {
                    result.data.apply {
                        this.networkWeatherCondition.temp =
                            convertKelvinToCelsius(this.networkWeatherCondition.temp)
                    }
                }
            }

            is Result.Error -> {}
            is Result.Loading -> {}
        }
        return result
    }
}
