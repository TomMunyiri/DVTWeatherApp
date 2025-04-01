package com.tommunyiri.dvtweatherapp.domain.usecases.weather

import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.domain.utils.convertKelvinToCelsius
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetSearchWeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(location: String): Flow<Result<Weather?>> {
        return weatherRepository.getSearchWeather(location)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data != null) {
                            result.data.apply {
                                this.networkWeatherCondition.temp =
                                    convertKelvinToCelsius(this.networkWeatherCondition.temp)
                            }
                        }
                        result
                    }
                    else -> result
                }
            }
    }
}
