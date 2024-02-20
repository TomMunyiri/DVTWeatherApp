package com.tommunyiri.dvtweatherapp.presentation.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.instantsearch.android.paging3.searchbox.connectPaginator
import com.algolia.instantsearch.android.paging3.Paginator
import com.algolia.instantsearch.compose.item.StatsTextState
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.instantsearch.stats.StatsConnector
import com.algolia.instantsearch.stats.StatsPresenterImpl
import com.algolia.instantsearch.stats.connectView
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.tommunyiri.dvtweatherapp.BuildConfig
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.SearchResult
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.repository.WeatherRepository
import com.tommunyiri.dvtweatherapp.presentation.screens.home.HomeScreenEvent
import com.tommunyiri.dvtweatherapp.presentation.screens.home.HomeScreenState
import com.tommunyiri.dvtweatherapp.utils.Result
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.utils.asLiveData
import com.tommunyiri.dvtweatherapp.utils.convertKelvinToCelsius
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
class SearchScreenViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val prefs: SharedPreferenceHelper
) :
    ViewModel() {

    private val applicationID = BuildConfig.ALGOLIA_APP_ID
    private val algoliaAPIKey = BuildConfig.ALGOLIA_API_KEY
    private val algoliaIndexName = BuildConfig.ALGOLIA_INDEX_NAME

    private val searcher = HitsSearcher(
        applicationID = ApplicationID(applicationID),
        apiKey = APIKey(algoliaAPIKey),
        indexName = IndexName(algoliaIndexName)
    )

    // Search Box
    val searchBoxState = SearchBoxState()
    private val searchBoxConnector = SearchBoxConnector(searcher)

    val statsText = StatsTextState()
    private val statsConnector = StatsConnector(searcher)

    // Hits
    val hitsPaginator = Paginator(searcher) { it.deserialize(SearchResult.serializer()) }

    private val connections = ConnectionHandler(searchBoxConnector, statsConnector)

    init {
        connections += searchBoxConnector.connectView(searchBoxState)
        connections += searchBoxConnector.connectPaginator(hitsPaginator)
        connections += statsConnector.connectView(statsText, StatsPresenterImpl())
    }

    var state by mutableStateOf(SearchScreenState())

    fun onEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.GetWeather -> {
                getSearchWeather(event.city)
            }
        }
    }

    fun getSharedPrefs(): SharedPreferenceHelper {
        return prefs
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

                else -> state = state.copy(isLoading = true, error = null)
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