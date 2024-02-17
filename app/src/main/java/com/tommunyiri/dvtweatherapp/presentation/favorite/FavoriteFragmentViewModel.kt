package com.tommunyiri.dvtweatherapp.presentation.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.asLiveData
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class FavoriteFragmentViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {
    private val _favoriteLocations = MutableLiveData<List<FavoriteLocation>?>()
    val favoriteLocation = _favoriteLocations.asLiveData()

    private val _isFavoriteLocationLoading = MutableLiveData<Boolean>()
    val isFavoriteLocationLoading = _isFavoriteLocationLoading.asLiveData()

    private val _dataFetchStateFavoriteLocations = MutableLiveData<Boolean>()
    val dataFetchStateFavoriteLocations = _dataFetchStateFavoriteLocations.asLiveData()

    private val _deleteFavoriteLocationResult = MutableLiveData<Int?>()
    val deleteFavoriteLocationResult = _deleteFavoriteLocationResult.asLiveData()

    private val _isFavoriteLocationDeletionLoading = MutableLiveData<Boolean>()
    val isFavoriteLocationDeletionLoading = _isFavoriteLocationDeletionLoading.asLiveData()

    private val _deleteStateFavoriteLocation = MutableLiveData<Boolean>()
    val deleteStateFavoriteLocation = _deleteStateFavoriteLocation.asLiveData()

    fun getFavoriteLocations() {
        _isFavoriteLocationLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getFavoriteLocations()) {
                is Result.Success -> {
                    _isFavoriteLocationLoading.postValue(false)
                    if (!result.data.isNullOrEmpty()) {
                        val forecasts = result.data
                        _dataFetchStateFavoriteLocations.value = true
                        _favoriteLocations.value = forecasts
                    } else {
                        //refresh
                    }
                }

                is Result.Loading -> {
                    _isFavoriteLocationLoading.postValue(true)
                }

                else -> {}
            }
        }
    }

    fun deleteFavoriteLocation(name: String) {
        _isFavoriteLocationDeletionLoading.value = true
        viewModelScope.launch {
            when (val result = repository.deleteFavoriteLocation(name)) {
                is Result.Success -> {
                    _isFavoriteLocationDeletionLoading.postValue(false)
                    val deleteResult = result.data
                    _deleteStateFavoriteLocation.value = true
                    _deleteFavoriteLocationResult.value = deleteResult
                }

                is Result.Loading -> {
                    _isFavoriteLocationDeletionLoading.postValue(true)
                }

                else -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _deleteFavoriteLocationResult.value = null
        _isFavoriteLocationDeletionLoading.value = false
    }
}