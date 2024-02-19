package com.tommunyiri.dvtweatherapp.presentation.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.LocationLiveData
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.utils.asLiveData
import com.tommunyiri.dvtweatherapp.utils.convertCelsiusToFahrenheit
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import com.tommunyiri.dvtweatherapp.utils.formatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
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
    private val prefs: SharedPreferenceHelper
) :
    ViewModel() {

    lateinit var location: LocationModel
    var state by mutableStateOf(HomeScreenState())

    init {
        currentSystemTime()
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            fetchLocation().collect { locationValue ->
                location = locationValue
                getWeather(location)
            }
        }
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()

    private val _weather = MutableLiveData<Weather?>()
    val weather = _weather.asLiveData()

    val time = currentSystemTime()

    private fun fetchLocation() = locationLiveData.asFlow().distinctUntilChanged().take(1)

    private val _forecast = MutableLiveData<List<WeatherForecast>?>()
    val forecast = _forecast.asLiveData()

    private val _isForecastLoading = MutableLiveData<Boolean>()
    val isForecastLoading = _isForecastLoading.asLiveData()

    private val _dataFetchStateForecast = MutableLiveData<Boolean>()
    val dataFetchStateForecast = _dataFetchStateForecast.asLiveData()

    private val _isWeatherRefresh = MutableLiveData<Boolean>()
    val isWeatherRefresh = _isWeatherRefresh.asLiveData()

    fun getSharedPrefs(): SharedPreferenceHelper {
        return prefs
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.Refresh -> {
                refreshWeather(location)
            }
        }
    }

    /**
     *This attempts to get the [WeatherForecast] from the local data source,
     * if the result is null, it gets from the remote source.
     * @see refreshForecastData
     */
    //fun getWeatherForecast(cityId: Int?) {
    fun getWeatherForecast(location: LocationModel) {
        viewModelScope.launch {
            when (val result = repository.getForecastWeather(location, false)) {
                is Result.Success -> {
                    _isForecastLoading.postValue(false)
                    if (!result.data.isNullOrEmpty()) {
                        val forecasts = result.data
                        _dataFetchStateForecast.value = true
                        _forecast.value = forecasts
                    } else {
                        refreshForecastData(location)
                        //refreshForecastData(cityId)
                    }
                }

                is Result.Loading -> {
                    _isForecastLoading.postValue(true)
                }

                is Result.Error -> {
                    _dataFetchStateForecast.value = false
                    _isForecastLoading.value = false
                }
            }
        }
    }

    //fun refreshForecastData(cityId: Int?) {
    fun refreshForecastData(location: LocationModel) {
        _isForecastLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getForecastWeather(location, true)) {
                is Result.Success -> {
                    _isForecastLoading.postValue(false)
                    if (result.data != null) {
                        val forecast = result.data.onEach { forecast ->
                            forecast.networkWeatherCondition.temp =
                                convertKelvinToCelsius(forecast.networkWeatherCondition.temp)
                            forecast.date = forecast.date.formatDate().toString()
                        }
                        _forecast.postValue(forecast)
                        _dataFetchStateForecast.postValue(true)
                        repository.deleteForecastData()
                        repository.storeForecastData(forecast)
                    } else {
                        _dataFetchStateForecast.postValue(false)
                        _forecast.postValue(null)
                    }
                }

                is Result.Error -> {
                    _dataFetchStateForecast.value = false
                    _isForecastLoading.value = false
                }

                is Result.Loading -> _isForecastLoading.postValue(true)
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
                        Log.d("TAG", "getWeather: $weather")
                        state = state.copy(isLoading = false, weather = weather, error = null)
                    } else {
                        refreshWeather(locationModel)
                    }
                }

                is Result.Error -> {
                    state = state.copy(isLoading = false, error = result.exception.toString())
                }

                is Result.Loading -> state = state.copy(isLoading = true)
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
                        repository.deleteWeatherData()
                        repository.storeWeatherData(weather)
                    } else {
                        state = state.copy(isLoading = false, error = "No weather data")
                    }
                }

                is Result.Error -> {
                    state = state.copy(isLoading = false, error = result.exception.toString())
                }

                is Result.Loading -> state = state.copy(isLoading = true)
            }
        }
    }
}