package com.tommunyiri.dvtweatherapp.presentation.screens.home


/**
 * Created by Tom Munyiri on 19/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

sealed class HomeScreenEvent {
    data object Refresh : HomeScreenEvent()
    data object GetForecast : HomeScreenEvent()
}