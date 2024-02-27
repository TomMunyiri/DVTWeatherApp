package com.tommunyiri.dvtweatherapp.presentation.screens.favorites


/**
 * Created by Tom Munyiri on 21/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
sealed class FavoritesScreenEvent {
    data class RemoveFromFavorite(val favoriteLocationName: String) : FavoritesScreenEvent()
    data class GetWeather(val city: String) : FavoritesScreenEvent()
    data object GetFavorites : FavoritesScreenEvent()
    data object ResetWeather : FavoritesScreenEvent()
    data object ResetDeleteFavoriteResult : FavoritesScreenEvent()
    data object ClearError : FavoritesScreenEvent()
}