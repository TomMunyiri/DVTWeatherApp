package com.tommunyiri.dvtweatherapp.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommunyiri.dvtweatherapp.data.model.LocationModel
import com.tommunyiri.dvtweatherapp.data.model.Weather
import com.tommunyiri.dvtweatherapp.data.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.data.source.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.LocationLiveData
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.asLiveData
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
import com.tommunyiri.dvtweatherapp.utils.formatDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class HomeFragmentViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    @Inject
    lateinit var locationLiveData: LocationLiveData

    init {
        currentSystemTime()
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()

    private val _weather = MutableLiveData<Weather?>()
    val weather = _weather.asLiveData()

    val time = currentSystemTime()

    fun fetchLocationLiveData() = locationLiveData

    private val _forecast = MutableLiveData<List<WeatherForecast>?>()
    val forecast = _forecast.asLiveData()

    private val _isForecastLoading = MutableLiveData<Boolean>()
    val isForecastLoading = _isForecastLoading.asLiveData()

    private val _dataFetchStateForecast = MutableLiveData<Boolean>()
    val dataFetchStateForecast = _dataFetchStateForecast.asLiveData()

    /**
     *This attempts to get the [WeatherForecast] from the local data source,
     * if the result is null, it gets from the remote source.
     * @see refreshForecastData
     */
    //fun getWeatherForecast(cityId: Int?) {
    fun getWeatherForecast(location: LocationModel) {
        _isForecastLoading.value = true
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

                else -> {}
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
                is Result.Error -> TODO()
                Result.Loading -> TODO()
                is Result.Success -> TODO()
            }
        }
    }

    /**
     *This attempts to get the [Weather] from the local data source,
     * if the result is null, it gets from the remote source.
     * @see refreshWeather
     */
    fun getWeather(location: LocationModel) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val result = repository.getWeather(location, false)) {
                is Result.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        val weather = result.data
                        _dataFetchState.value = true
                        _weather.value = weather
                    } else {
                        refreshWeather(location)
                    }
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                }

                is Result.Loading -> _isLoading.postValue(true)
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
    fun refreshWeather(location: LocationModel) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getWeather(location, true)) {
                is Result.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        val weather = result.data.apply {
                            this.networkWeatherCondition.temp =
                                convertKelvinToCelsius(this.networkWeatherCondition.temp)
                            this.networkWeatherCondition.tempMax =
                                convertKelvinToCelsius(this.networkWeatherCondition.tempMax)
                            this.networkWeatherCondition.tempMin =
                                convertKelvinToCelsius(this.networkWeatherCondition.tempMin)
                        }
                        _dataFetchState.value = true
                        _weather.value = weather

                        repository.deleteWeatherData()
                        repository.storeWeatherData(weather)
                    } else {
                        _weather.postValue(null)
                        _dataFetchState.postValue(false)
                    }
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                }

                is Result.Loading -> _isLoading.postValue(true)
            }
        }
    }
}