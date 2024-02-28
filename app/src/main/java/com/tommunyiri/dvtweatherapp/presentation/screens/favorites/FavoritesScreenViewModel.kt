package com.tommunyiri.dvtweatherapp.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.usecases.preferences.GetSharedPreferencesUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases
import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.data.repository.LocationRepository
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 21/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltViewModel
class FavoritesScreenViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherUseCases: WeatherUseCases,
    private val getPrefsUseCase: GetSharedPreferencesUseCase
) : ViewModel() {

    private val _favoritesScreenState = MutableStateFlow(FavoritesScreenState())
    val favoritesScreenState: StateFlow<FavoritesScreenState> = _favoritesScreenState.asStateFlow()

    private val _location = MutableStateFlow(LocationModel(0.00, 0.00))
    val location: StateFlow<LocationModel> = _location.asStateFlow()

    init {
        getFavoriteLocations()
        getCurrentLocation()
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

            //resets previously searched weather when bottom sheet is closed
            is FavoritesScreenEvent.ResetWeather -> _favoritesScreenState.update { currentState ->
                currentState.copy(weather = null)
            }

            is FavoritesScreenEvent.ResetDeleteFavoriteResult -> _favoritesScreenState.update { currentState ->
                currentState.copy(deleteFavoriteResult = null)
            }

            is FavoritesScreenEvent.ClearError -> _favoritesScreenState.update { currentState ->
                currentState.copy(
                    error = null
                )
            }
        }
    }

    private fun getFavoriteLocations() {
        setLoading()
        viewModelScope.launch {
            when (val result = weatherUseCases.getFavoriteLocations.invoke()) {
                is Result.Success -> {
                    if (!result.data.isNullOrEmpty()) {
                        _favoritesScreenState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                favoriteLocationsList = result.data,
                                error = null
                            )
                        }
                    } else {
                        _favoritesScreenState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                favoriteLocationsList = emptyList(),
                                error = null
                            )
                        }
                    }
                }

                is Result.Loading ->
                    _favoritesScreenState.update { currentState ->
                        currentState.copy(isLoading = true, error = null)
                    }

                is Result.Error -> _favoritesScreenState.update { currentState ->
                    currentState.copy(isLoading = false, error = result.exception.toString())
                }
            }
        }
    }

    /**
     * Gets the [Weather] information for the user selected location[name]
     * @param name value of the location whose [Weather] data is to be fetched.
     */
    private fun getSearchWeather(name: String) {
        setLoading()
        viewModelScope.launch {
            when (val result = weatherUseCases.getSearchWeather.invoke(name)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _favoritesScreenState.update { currentState ->
                            currentState.copy(
                                weather = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        _favoritesScreenState.update { currentState ->
                            currentState.copy(
                                weather = null,
                                isLoading = false,
                                error = "No weather data at the moment"
                            )
                        }
                    }
                }

                is Result.Error -> _favoritesScreenState.update { currentState ->
                    currentState.copy(
                        weather = null,
                        isLoading = false,
                        error = result.exception.toString()
                    )
                }

                is Result.Loading -> _favoritesScreenState.update { currentState ->
                    currentState.copy(isLoading = true, error = null)
                }
            }
        }
    }

    private fun deleteFavoriteLocation(name: String) {
        setLoading()
        viewModelScope.launch {
            when (val result = weatherUseCases.deleteFavoriteLocation.invoke(name)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _favoritesScreenState.update { currentState ->
                            currentState.copy(
                                deleteFavoriteResult = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                        getFavoriteLocations()
                    }
                }

                is Result.Loading -> _favoritesScreenState.update { currentState ->
                    currentState.copy(isLoading = true, error = null, deleteFavoriteResult = null)
                }

                is Result.Error -> _favoritesScreenState.update { currentState ->
                    currentState.copy(
                        deleteFavoriteResult = 0,
                        weather = null,
                        isLoading = false,
                        error = result.exception.toString()
                    )
                }

                null -> {}
            }
        }
    }

    private fun setLoading() {
        _favoritesScreenState.update { currentSate ->
            currentSate.copy(isLoading = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _favoritesScreenState.update { currentState ->
            currentState.copy(favoriteLocationsList = null)
        }
        locationRepository.stopLocationUpdates()
    }

    fun getSharedPrefs(): SharedPreferenceHelper {
        return getPrefsUseCase.invoke()
    }

    private fun getCurrentLocation() {
        locationRepository.startLocationUpdates()
        locationRepository.locationStateFlow
            .onEach { locationValue ->
                if (locationValue != null) {
                    _location.update { locationValue }
                }
            }
            .launchIn(viewModelScope)
    }

}