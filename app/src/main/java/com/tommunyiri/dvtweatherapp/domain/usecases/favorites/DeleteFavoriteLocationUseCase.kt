package com.tommunyiri.dvtweatherapp.domain.usecases.favorites

import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class DeleteFavoriteLocationUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(name: String): Flow<Result<Int>> {
        return weatherRepository.deleteFavoriteLocation(name)
    }
}
