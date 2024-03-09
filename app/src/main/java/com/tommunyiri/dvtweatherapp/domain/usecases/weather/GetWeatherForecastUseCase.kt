package com.tommunyiri.dvtweatherapp.domain.usecases.weather

import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.domain.utils.convertKelvinToCelsius
import com.tommunyiri.dvtweatherapp.domain.utils.formatDate

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetWeatherForecastUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(
        location: LocationModel,
        refresh: Boolean,
    ): Result<List<WeatherForecast>?> {
        val result = weatherRepository.getForecastWeather(location, refresh)
        if (refresh) {
            when (result) {
                is Result.Success -> {
                    if (result.data != null) {
                        result.data.onEach { forecast ->
                            forecast.networkWeatherCondition.temp =
                                convertKelvinToCelsius(forecast.networkWeatherCondition.temp)
                            forecast.date = forecast.date.formatDate().toString()
                        }
                        weatherRepository.deleteForecastData()
                        weatherRepository.storeForecastData(result.data)
                    }
                }

                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
        return result
    }
}
