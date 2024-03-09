package com.tommunyiri.dvtweatherapp.presentation.screens.search

import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation

/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
sealed class SearchScreenEvent {
    data class GetWeather(val city: String) : SearchScreenEvent()

    data class AddToFavorite(val favoriteLocation: FavoriteLocation) : SearchScreenEvent()

    data object ResetWeather : SearchScreenEvent()

    data object ResetAddToFavoriteResult : SearchScreenEvent()

    data object ClearError : SearchScreenEvent()
}
