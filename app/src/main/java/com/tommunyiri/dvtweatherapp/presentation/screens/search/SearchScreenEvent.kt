package com.tommunyiri.dvtweatherapp.presentation.screens.search


/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
sealed class SearchScreenEvent {
    data class OnSearchQueryChange(val query: String) : SearchScreenEvent()
}