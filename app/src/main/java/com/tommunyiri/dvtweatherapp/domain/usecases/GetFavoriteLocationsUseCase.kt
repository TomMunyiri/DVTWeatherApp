package com.tommunyiri.dvtweatherapp.domain.usecases

import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.Result


/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetFavoriteLocationsUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(): Result<List<FavoriteLocation>?> {
        return weatherRepository.getFavoriteLocations()
    }
}