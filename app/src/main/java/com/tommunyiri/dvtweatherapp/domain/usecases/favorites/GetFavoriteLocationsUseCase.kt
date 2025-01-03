package com.tommunyiri.dvtweatherapp.domain.usecases.favorites

import android.util.Log
import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetFavoriteLocationsUseCase(private val weatherRepository: WeatherRepository) {
    operator fun invoke(): Flow<Result<List<FavoriteLocation>?>> {
        return weatherRepository.getFavoriteLocations()
    }
}
