package com.tommunyiri.dvtweatherapp.presentation.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 21/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltViewModel
class FavoritesScreenViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val prefs: SharedPreferenceHelper
) :
    ViewModel() {
    var state by mutableStateOf(FavoritesScreenState())

    init {
        getFavoriteLocations()
    }

    fun onEvent(event: FavoritesScreenEvent) {
        when (event) {
            is FavoritesScreenEvent.RemoveFromFavorite -> {
                deleteFavoriteLocation(event.favoriteLocationName)
            }

            is FavoritesScreenEvent.GetFavorites -> {
                getFavoriteLocations()
            }

            is FavoritesScreenEvent.GetWeather -> {
                getSearchWeather(event.city)
            }
        }
    }

    private fun getFavoriteLocations() {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getFavoriteLocations()) {
                is Result.Success -> {
                    state = if (!result.data.isNullOrEmpty()) {
                        state.copy(
                            isLoading = false,
                            favoriteLocationsList = result.data,
                            error = null
                        )
                    } else {
                        state.copy(
                            isLoading = false,
                            favoriteLocationsList = emptyList(),
                            error = null
                        )
                    }
                }

                is Result.Loading ->
                    state = state.copy(isLoading = true, error = null)

                is Result.Error -> state =
                    state.copy(isLoading = false, error = result.exception.toString())
            }
        }
    }

    /**
     * Gets the [Weather] information for the user selected location[name]
     * @param name value of the location whose [Weather] data is to be fetched.
     */
    private fun getSearchWeather(name: String) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getSearchWeather(name)) {
                is Result.Success -> {
                    if (result.data != null) {
                        val weatherData = result.data.apply {
                            this.networkWeatherCondition.temp =
                                convertKelvinToCelsius(this.networkWeatherCondition.temp)
                        }
                        state = state.copy(
                            weather = weatherData,
                            isLoading = false,
                            error = null
                        )
                    } else {
                        state = state.copy(
                            weather = null,
                            isLoading = false,
                            error = "No weather data at the moment"
                        )
                    }
                }

                is Result.Error -> state = state.copy(
                    weather = null,
                    isLoading = false,
                    error = result.exception.toString()
                )

                is Result.Loading -> state = state.copy(isLoading = true, error = null)
            }
        }
    }

    private fun deleteFavoriteLocation(name: String) {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.deleteFavoriteLocation(name)) {
                is Result.Success -> {
                    if (result.data != null) {
                        state = state.copy(
                            deleteFavoriteResult = result.data,
                            isLoading = false,
                            error = null
                        )
                        getFavoriteLocations()
                    }
                }

                is Result.Loading -> state =
                    state.copy(isLoading = true, error = null, deleteFavoriteResult = null)

                is Result.Error -> state = state.copy(
                    deleteFavoriteResult = 0,
                    weather = null,
                    isLoading = false,
                    error = result.exception.toString()
                )

                null -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        state = state.copy(favoriteLocationsList = null)
    }

    fun getSharedPrefs(): SharedPreferenceHelper {
        return prefs
    }

}