package com.tommunyiri.dvtweatherapp.presentation.screens.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.instantsearch.android.paging3.searchbox.connectPaginator
import com.algolia.instantsearch.android.paging3.Paginator
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.tommunyiri.dvtweatherapp.BuildConfig
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.SearchResult
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@HiltViewModel
class SearchScreenViewModel @Inject constructor(private val repository: WeatherRepository) :
    ViewModel() {

    private val applicationID = BuildConfig.ALGOLIA_APP_ID
    private val algoliaAPIKey = BuildConfig.ALGOLIA_API_KEY
    private val algoliaIndexName = BuildConfig.ALGOLIA_INDEX_NAME

    val searcher = HitsSearcher(
        applicationID = ApplicationID(applicationID),
        apiKey = APIKey(algoliaAPIKey),
        indexName = IndexName(algoliaIndexName)
    )

    // Search Box
    val searchBoxState = SearchBoxState()
    val searchBoxConnector = SearchBoxConnector(searcher)

    // Hits
    val hitsPaginator = Paginator(searcher) { it.deserialize(SearchResult.serializer()) }

    val connections = ConnectionHandler(searchBoxConnector)

    init {
        connections += searchBoxConnector.connectView(searchBoxState)
        connections += searchBoxConnector.connectPaginator(hitsPaginator)
    }

    private val _weatherInfo = MutableLiveData<Weather?>()
    val weatherInfo = _weatherInfo.asLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()

    /**
     * Gets the [Weather] information for the user selected location[name]
     * @param name value of the location whose [Weather] data is to be fetched.
     */
    fun getSearchWeather(name: String) {
        _isLoading.postValue(true)
        _weatherInfo.postValue(null)
        viewModelScope.launch {
            when (val result = repository.getSearchWeather(name)) {
                is Result.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        Timber.i("Result ${result.data}")
                        _dataFetchState.value = true
                        _weatherInfo.postValue(result.data)
                    } else {
                        _weatherInfo.postValue(null)
                        _dataFetchState.postValue(false)
                    }
                }

                is Result.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                }

                else -> {}
            }
        }
    }

    fun saveFavoriteLocation(favoriteLocation: FavoriteLocation) {
        viewModelScope.launch {
            repository.storeFavoriteLocationData(favoriteLocation)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
    }

}