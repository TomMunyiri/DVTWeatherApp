package com.tommunyiri.dvtweatherapp.presentation.screens.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.core.worker.UpdateWeatherWorker
import com.tommunyiri.dvtweatherapp.data.repository.LocationRepository
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.usecases.preferences.GetSharedPreferencesUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

/**
 * Created by Tom Munyiri on 19/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltViewModel
class HomeScreenViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val getPrefsUseCase: GetSharedPreferencesUseCase,
        private val weatherUseCases: WeatherUseCases,
        private val context: Application,
    ) : ViewModel() {
        private val _homeScreenState = MutableStateFlow(HomeScreenState())
        val homeScreenState: StateFlow<HomeScreenState> = _homeScreenState.asStateFlow()
        lateinit var location: LocationModel

        val time = currentSystemTime()
        val prefs = getSharedPrefs()

        init {
            locationRepository.startLocationUpdates()
            currentSystemTime()
            setIsWeatherLoading()
            locationRepository.locationStateFlow.take(2)
                .onEach { locationValue ->
                    if (locationValue != null) {
                        location = locationValue
                        getWeather(location)
                        setupWorkManager()
                    }
                }
                .launchIn(viewModelScope)
        }

        fun onEvent(event: HomeScreenEvent) {
            when (event) {
                is HomeScreenEvent.Refresh -> {
                    locationRepository.locationStateFlow.take(1)
                        .onEach { locationValue ->
                            if (locationValue != null) {
                                location = locationValue
                                refreshWeather(location)
                            }
                        }
                        .launchIn(viewModelScope)
                }

                is HomeScreenEvent.GetForecast -> {
                    setIsWeatherForecastLoading()
                    locationRepository.locationStateFlow.take(1)
                        .onEach { locationValue ->
                            if (locationValue != null) {
                                location = locationValue
                                getWeatherForecast(location)
                            }
                        }
                        .launchIn(viewModelScope)
                }

                is HomeScreenEvent.ClearError ->
                    _homeScreenState.update { currentState ->
                        currentState.copy(
                            error = null,
                        )
                    }
            }
        }

        /**
         *This attempts to get the [WeatherForecast] from the local data source,
         * if the result is null, it gets from the remote source.
         * @see refreshForecastData
         */
        private fun getWeatherForecast(locationModel: LocationModel) {
            setIsWeatherForecastLoading()
            viewModelScope.launch {
                when (val result = weatherUseCases.getWeatherForecast(locationModel, false)) {
                    is Result.Success -> {
                        if (!result.data.isNullOrEmpty()) {
                            _homeScreenState.update { currentState ->
                                currentState.copy(
                                    isLoadingForecast = false,
                                    weatherForecastList = result.data,
                                    error = null,
                                )
                            }
                        } else {
                            refreshForecastData(location)
                        }
                    }

                    is Result.Loading ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(isLoadingForecast = true, error = null)
                        }

                    is Result.Error ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(
                                isRefreshing = false,
                                isLoadingForecast = false,
                                error = result.exception.toString(),
                            )
                        }
                }
            }
        }

        private fun refreshForecastData(locationModel: LocationModel) {
            setIsWeatherForecastLoading()
            viewModelScope.launch {
                when (val result = weatherUseCases.getWeatherForecast(locationModel, true)) {
                    is Result.Success -> {
                        if (result.data != null) {
                            _homeScreenState.update { currentState ->
                                currentState.copy(
                                    isLoadingForecast = false,
                                    weatherForecastList = result.data,
                                    error = null,
                                )
                            }
                        } else {
                            refreshForecastData(locationModel)
                        }
                    }

                    is Result.Error ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(
                                isRefreshing = false,
                                isLoadingForecast = false,
                                error = result.exception.toString(),
                            )
                        }

                    is Result.Loading ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(isLoadingForecast = true, error = null)
                        }
                }
            }
        }

        /**
         *This attempts to get the [Weather] from the local data source,
         * if the result is null, it gets from the remote source.
         * @see refreshWeather
         */
        private fun getWeather(locationModel: LocationModel) {
            viewModelScope.launch {
                when (val result = weatherUseCases.getWeather(locationModel, false)) {
                    is Result.Success -> {
                        if (result.data != null) {
                            val weather = result.data
                            _homeScreenState.update { currentState ->
                                currentState.copy(isLoading = false, weather = weather, error = null)
                            }
                            getWeatherForecast(locationModel)
                        } else {
                            refreshWeather(locationModel)
                        }
                    }

                    is Result.Error ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(
                                isRefreshing = false,
                                isLoading = false,
                                error = result.exception.toString(),
                            )
                        }

                    is Result.Loading ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(isLoading = true, error = null)
                        }
                }
            }
        }

        /**
         * This is called when the user swipes down to refresh.
         * This enables the [Weather] for the current [location] to be received.
         */
        private fun refreshWeather(locationModel: LocationModel = location) {
            setIsWeatherLoading()
            viewModelScope.launch {
                when (val result = weatherUseCases.getWeather(locationModel, true)) {
                    is Result.Success -> {
                        if (result.data != null) {
                            _homeScreenState.update { currentState ->
                                currentState.copy(
                                    isLoading = false,
                                    weather = result.data,
                                    error = null,
                                )
                            }
                            refreshForecastData(locationModel)
                        } else {
                            _homeScreenState.update { currentState ->
                                currentState.copy(isLoading = false, error = "No weather data")
                            }
                        }
                    }

                    is Result.Error ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(
                                isRefreshing = false,
                                isLoading = false,
                                error = result.exception.toString(),
                            )
                        }

                    is Result.Loading ->
                        _homeScreenState.update { currentState ->
                            currentState.copy(isLoading = true)
                        }
                }
            }
        }

        private fun setIsWeatherLoading() {
            _homeScreenState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    isRefreshing = true,
                )
            }
        }

        private fun setIsWeatherForecastLoading() {
            _homeScreenState.update { currentState ->
                currentState.copy(
                    isLoadingForecast = true,
                    isRefreshing = false,
                )
            }
        }

        private fun getSharedPrefs(): SharedPreferenceHelper {
            return getPrefsUseCase.invoke()
        }

        @SuppressLint("SimpleDateFormat")
        fun currentSystemTime(): String {
            val currentTime = System.currentTimeMillis()
            val date = Date(currentTime)
            val dateFormat = SimpleDateFormat("EEEE MMM d, hh:mm aaa")
            return dateFormat.format(date)
        }

        override fun onCleared() {
            super.onCleared()
            locationRepository.stopLocationUpdates()
        }

        private fun setupWorkManager() {
            var cacheDuration by Delegates.notNull<Long>()
            prefs.apply {
                saveLocation(location)
                cacheDuration = this.getUserSetCacheDuration().toString().toLong()
            }

            val constraint =
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            // Note: The minimum repeat interval that can be defined is 15 minutes (same as the JobScheduler API).
            val weatherUpdateRequest =
                PeriodicWorkRequestBuilder<UpdateWeatherWorker>(cacheDuration, TimeUnit.HOURS)
                    .setConstraints(constraint)
                    .setInitialDelay(1, TimeUnit.MINUTES)
                    /** retry work if it fails after 10 seconds
                     * Since the policy is LINEAR the retry interval will increase by approximately 10 seconds with each new attempt
                     * For instance, the first run finishing with Result.retry() will be attempted again after 10 seconds,
                     * followed by 20, 30, 40, and so on
                     **/
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.SECONDS,
                    )
                    .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "dvt_update_weather_worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                weatherUpdateRequest,
            )
        }
    }
