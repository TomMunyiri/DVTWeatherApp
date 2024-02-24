package com.tommunyiri.dvtweatherapp.presentation.screens.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.LocationLiveData
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.utils.asLiveData
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import com.tommunyiri.dvtweatherapp.utils.formatDate
import com.tommunyiri.dvtweatherapp.worker.UpdateWeatherWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
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
    private val repository: WeatherRepository,
    private val locationLiveData: LocationLiveData,
    private val prefs: SharedPreferenceHelper,
    private val context: Application
) : ViewModel() {

    lateinit var location: LocationModel
    var state by mutableStateOf(HomeScreenState())
    private val _weather = MutableLiveData<Weather?>()
    val weather = _weather.asLiveData()

    val time = currentSystemTime()

    init {
        currentSystemTime()
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            fetchLocation().collect { locationValue ->
                location = locationValue
                getWeather(location)
                setupWorkManager()
            }
        }
    }

    private fun fetchLocation() = locationLiveData.asFlow().distinctUntilChanged().take(1)

    fun getSharedPrefs(): SharedPreferenceHelper {
        return prefs
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
                state = state.copy(isLoadingForecast = true)
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
        state = state.copy(isLoadingForecast = true, isRefreshing = false)
        viewModelScope.launch {
            when (val result = repository.getForecastWeather(locationModel, false)) {
                is Result.Success -> {
                    if (!result.data.isNullOrEmpty()) {
                        val forecasts = result.data
                        state = state.copy(
                            isLoadingForecast = false,
                            weatherForecastList = forecasts,
                            error = null
                        )
                    } else {
                        refreshForecastData(location)
                        //refreshForecastData(cityId)
                    }
                }

                is Result.Loading ->
                    state = state.copy(isLoadingForecast = true, error = null)

                is Result.Error ->
                    state =
                        state.copy(isLoadingForecast = false, error = result.exception.toString())

            }
        }
    }

    //fun refreshForecastData(cityId: Int?) {
    private fun refreshForecastData(locationModel: LocationModel) {
        state = state.copy(isLoadingForecast = true)
        viewModelScope.launch {
            when (val result = repository.getForecastWeather(locationModel, true)) {
                is Result.Success -> {
                    if (result.data != null) {
                        val forecast = result.data.onEach { forecast ->
                            forecast.networkWeatherCondition.temp =
                                convertKelvinToCelsius(forecast.networkWeatherCondition.temp)
                            forecast.date = forecast.date.formatDate().toString()
                        }
                        state = state.copy(
                            isLoadingForecast = false,
                            weatherForecastList = forecast,
                            error = null
                        )
                        repository.deleteForecastData()
                        repository.storeForecastData(forecast)
                    } else {
                        refreshForecastData(locationModel)
                    }
                }

                is Result.Error -> state =
                    state.copy(isLoadingForecast = false, error = result.exception.toString())

                is Result.Loading -> state = state.copy(isLoadingForecast = true, error = null)
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
            when (val result = repository.getWeather(locationModel, false)) {
                is Result.Success -> {
                    if (result.data != null) {
                        val weather = result.data
                        state = state.copy(isLoading = false, weather = weather, error = null)
                        getWeatherForecast(locationModel)
                    } else {
                        refreshWeather(locationModel)
                    }
                }

                is Result.Error ->
                    state = state.copy(isLoading = false, error = result.exception.toString())

                is Result.Loading -> state = state.copy(isLoading = true, error = null)
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
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getWeather(locationModel, true)) {
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
                        state = state.copy(isLoading = false, weather = weather, error = null)
                        refreshForecastData(locationModel)
                        repository.deleteWeatherData()
                        repository.storeWeatherData(weather)
                    } else {
                        state = state.copy(isLoading = false, error = "No weather data")
                    }
                }

                is Result.Error ->
                    state = state.copy(isLoading = false, error = result.exception.toString())

                is Result.Loading -> state = state.copy(isLoading = true)
            }
        }
    }
}