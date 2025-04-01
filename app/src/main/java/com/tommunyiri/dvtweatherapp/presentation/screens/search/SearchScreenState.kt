package com.tommunyiri.dvtweatherapp.presentation.screens.search

import com.tommunyiri.dvtweatherapp.domain.model.Weather

/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
data class SearchScreenState(
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val addToFavoriteResult: Long? = null,
)
