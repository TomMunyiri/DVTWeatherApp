package com.tommunyiri.dvtweatherapp.domain.usecases

import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.domain.utils.convertKelvinToCelsius

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetWeatherUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(location: LocationModel, refresh: Boolean): Result<Weather?> {
        val result = weatherRepository.getWeather(location, refresh)
        if (refresh) {
            when (result) {
                is Result.Success -> {
                    if (result.data != null) {
                        result.data.apply {
                            this.networkWeatherCondition.temp =
                                convertKelvinToCelsius(this.networkWeatherCondition.temp)
                            this.networkWeatherCondition.tempMax =
                                convertKelvinToCelsius(this.networkWeatherCondition.tempMax)
                            this.networkWeatherCondition.tempMin =
                                convertKelvinToCelsius(this.networkWeatherCondition.tempMin)
                        }
                        weatherRepository.deleteWeatherData()
                        weatherRepository.storeWeatherData(result.data)
                    }
                }

                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
        return result
    }
}