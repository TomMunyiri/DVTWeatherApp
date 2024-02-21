package com.tommunyiri.dvtweatherapp.presentation.screens.favorites

import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.Weather


/**
 * Created by Tom Munyiri on 21/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

data class FavoritesScreenState(
    val favoriteLocationsList: List<FavoriteLocation>? = null,
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val deleteFavoriteResult: Int? = null
)