package com.tommunyiri.dvtweatherapp.domain.usecases.weather

import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.domain.utils.convertKelvinToCelsius
import com.tommunyiri.dvtweatherapp.domain.utils.formatDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetWeatherForecastUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(
        location: LocationModel,
        refresh: Boolean,
    ): Flow<Result<List<WeatherForecast>?>> {
        return weatherRepository.getForecastWeather(location, refresh)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data != null && refresh) {
                            result.data.onEach { forecast ->
                                forecast.networkWeatherCondition.temp =
                                    convertKelvinToCelsius(forecast.networkWeatherCondition.temp)
                                forecast.date = forecast.date.formatDate().toString()
                            }
                            weatherRepository.deleteForecastData()
                            weatherRepository.storeForecastData(result.data)
                        }
                        result
                    }
                    else -> result
                }
            }
    }
}
