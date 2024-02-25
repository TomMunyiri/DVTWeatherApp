package com.tommunyiri.dvtweatherapp.presentation.screens.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.usecases.GetSharedPreferencesUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.WeatherUseCases
import com.tommunyiri.dvtweatherapp.utils.LocationLiveData
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import com.tommunyiri.dvtweatherapp.utils.formatDate
import com.tommunyiri.dvtweatherapp.worker.UpdateWeatherWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 19/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val locationLiveData: LocationLiveData,
    private val getPrefsUseCase: GetSharedPreferencesUseCase,
    private val weatherUseCases: WeatherUseCases,
    private val context: Application
) : ViewModel() {

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState: StateFlow<HomeScreenState> = _homeScreenState.asStateFlow()
    lateinit var location: LocationModel

    val time = currentSystemTime()

    init {
        currentSystemTime()
        setIsWeatherLoading()
        viewModelScope.launch {
            fetchLocation().collect { locationValue ->
                location = locationValue
                getWeather(location)
                setupWorkManager()
            }
        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.Refresh -> {
                viewModelScope.launch {
                    fetchLocation().collect { locationValue ->
                        location = locationValue
                        refreshWeather(location)
                    }
                }
            }

            HomeScreenEvent.GetForecast -> {
                setIsWeatherForecastLoading()
                viewModelScope.launch {
                    fetchLocation().collect { locationValue ->
                        location = locationValue
                        getWeatherForecast(location)
                    }
                }
            }
        }
    }

    /**
     *This attempts to get the [WeatherForecast] from the local data source,
     * if the result is null, it gets from the remote source.
     * @see refreshForecastData
     */
    //fun getWeatherForecast(cityId: Int?) {
    private fun getWeatherForecast(locationModel: LocationModel) {
        //state = state.copy(isLoadingForecast = true, isRefreshing = false)
        setIsWeatherForecastLoading()
        viewModelScope.launch {
            when (val result = weatherUseCases.getWeatherForecast(locationModel, false)) {
                is Result.Success -> {
                    if (!result.data.isNullOrEmpty()) {
                        val forecasts = result.data
                        _homeScreenState.update { currentState ->
                            currentState.copy(
                                isLoadingForecast = false,
                                weatherForecastList = forecasts,
                                error = null
                            )
                        }
                    } else {
                        refreshForecastData(location)
                        //refreshForecastData(cityId)
                    }
                }

                is Result.Loading ->
                    _homeScreenState.update { currentState ->
                        currentState.copy(isLoadingForecast = true, error = null)
                    }

                is Result.Error ->
                    _homeScreenState.update { currentState ->
                        currentState.copy(
                            isLoadingForecast = false,
                            error = result.exception.toString()
                        )
                    }

            }
        }
    }

    //fun refreshForecastData(cityId: Int?) {
    private fun refreshForecastData(locationModel: LocationModel) {
        setIsWeatherForecastLoading()
        viewModelScope.launch {
            when (val result = weatherUseCases.getWeatherForecast(locationModel, true)) {
                is Result.Success -> {
                    if (result.data != null) {
                        val forecast = result.data.onEach { forecast ->
                            forecast.networkWeatherCondition.temp =
                                convertKelvinToCelsius(forecast.networkWeatherCondition.temp)
                            forecast.date = forecast.date.formatDate().toString()
                        }
                        _homeScreenState.update { currentState ->
                            currentState.copy(
                                isLoadingForecast = false,
                                weatherForecastList = forecast,
                                error = null
                            )
                        }
                        weatherUseCases.deleteWeatherForecast.invoke()
                        weatherUseCases.storeWeatherForecast.invoke(forecast)
                    } else {
                        refreshForecastData(locationModel)
                    }
                }

                is Result.Error -> _homeScreenState.update { currentState ->
                    currentState.copy(
                        isLoadingForecast = false,
                        error = result.exception.toString()
                    )
                }

                is Result.Loading -> _homeScreenState.update { currentState ->
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

                is Result.Error -> _homeScreenState.update { currentState ->
                    currentState.copy(isLoading = false, error = result.exception.toString())
                }

                is Result.Loading -> _homeScreenState.update { currentState ->
                    currentState.copy(isLoading = true, error = null)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun currentSystemTime(): String {
        val currentTime = System.currentTimeMillis()
        val date = Date(currentTime)
        val dateFormat = SimpleDateFormat("EEEE MMM d, hh:mm aaa")
        return dateFormat.format(date)
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
                        val weather = result.data.apply {
                            this.networkWeatherCondition.temp =
                                convertKelvinToCelsius(this.networkWeatherCondition.temp)
                            this.networkWeatherCondition.tempMax =
                                convertKelvinToCelsius(this.networkWeatherCondition.tempMax)
                            this.networkWeatherCondition.tempMin =
                                convertKelvinToCelsius(this.networkWeatherCondition.tempMin)
                        }
                        _homeScreenState.update { currentState ->
                            currentState.copy(isLoading = false, weather = weather, error = null)
                        }
                        refreshForecastData(locationModel)
                        weatherUseCases.deleteWeatherData.invoke()
                        weatherUseCases.storeWeatherData.invoke(weather)
                    } else {
                        _homeScreenState.update { currentState ->
                            currentState.copy(isLoading = false, error = "No weather data")
                        }
                    }
                }

                is Result.Error -> _homeScreenState.update { currentState ->
                    currentState.copy(isLoading = false, error = result.exception.toString())
                }

                is Result.Loading -> _homeScreenState.update { currentState ->
                    currentState.copy(isLoading = true)
                }
            }
        }
    }

    private fun setIsWeatherLoading() {
        _homeScreenState.update { currentState ->
            currentState.copy(
                isLoading = true,
                isRefreshing = true
            )
        }
    }

    private fun setIsWeatherForecastLoading() {
        _homeScreenState.update { currentState ->
            currentState.copy(
                isLoadingForecast = true,
                isRefreshing = false
            )
        }
    }

    private fun fetchLocation() = locationLiveData.asFlow().distinctUntilChanged().take(1)

    fun getSharedPrefs(): SharedPreferenceHelper {
        return getPrefsUseCase.invoke()
    }

    private fun setupWorkManager() {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val weatherUpdateRequest =
            PeriodicWorkRequestBuilder<UpdateWeatherWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraint)
                .setInitialDelay(10, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DVT_update_weather_worker",
            ExistingPeriodicWorkPolicy.UPDATE, weatherUpdateRequest
        )
    }
}