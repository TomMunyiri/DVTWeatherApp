package com.tommunyiri.dvtweatherapp.domain.usecases.favorites

import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class DeleteFavoriteLocationUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(name: String): Result<Int>? {
        return weatherRepository.deleteFavoriteLocation(name)
    }
}
