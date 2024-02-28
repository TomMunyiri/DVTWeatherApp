package com.tommunyiri.dvtweatherapp.presentation.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.instantsearch.android.paging3.Paginator
import com.algolia.instantsearch.android.paging3.searchbox.connectPaginator
import com.algolia.instantsearch.compose.item.StatsTextState
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.searchbox.connectView
import com.algolia.instantsearch.searcher.hits.HitsSearcher
import com.algolia.instantsearch.stats.DefaultStatsPresenter
import com.algolia.instantsearch.stats.StatsConnector
import com.algolia.instantsearch.stats.connectView
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.tommunyiri.dvtweatherapp.BuildConfig
import com.tommunyiri.dvtweatherapp.core.utils.Result
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.SearchResult
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.usecases.preferences.GetSharedPreferencesUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val weatherUseCases: WeatherUseCases,
    private val getPrefsUseCase: GetSharedPreferencesUseCase
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow(SearchScreenState())
    val searchScreenState: StateFlow<SearchScreenState> = _searchScreenState.asStateFlow()

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
        connections += statsConnector.connectView(statsText, DefaultStatsPresenter())
    }

    fun onEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.GetWeather -> {
                getSearchWeather(event.city)
            }

            is SearchScreenEvent.AddToFavorite -> {
                saveFavoriteLocation(event.favoriteLocation)
            }

            //resets previously searched weather when bottom sheet is closed
            is SearchScreenEvent.ResetWeather -> _searchScreenState.update { currentState ->
                currentState.copy(weather = null)
            }

            is SearchScreenEvent.ResetAddToFavoriteResult -> _searchScreenState.update { currentState ->
                currentState.copy(addToFavoriteResult = null)
            }

            is SearchScreenEvent.ClearError -> _searchScreenState.update { currentState ->
                currentState.copy(
                    error = null
                )
            }
        }
    }

    fun getSharedPrefs(): SharedPreferenceHelper {
        return getPrefsUseCase.invoke()
    }

    /**
     * Gets the [Weather] information for the user selected location[name]
     * @param name value of the location whose [Weather] data is to be fetched.
     */
    private fun getSearchWeather(name: String) {
        _searchScreenState.update { currentState ->
            currentState.copy(isLoading = true)
        }
        viewModelScope.launch {
            when (val result = weatherUseCases.getSearchWeather.invoke(name)) {
                is Result.Success -> {
                    if (result.data != null) {
                        _searchScreenState.update { currentState ->
                            currentState.copy(
                                weather = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        _searchScreenState.update { currentState ->
                            currentState.copy(
                                weather = null,
                                isLoading = false,
                                error = "No weather data at the moment"
                            )
                        }
                    }
                }

                is Result.Error -> _searchScreenState.update { currentState ->
                    currentState.copy(
                        weather = null,
                        isLoading = false,
                        error = result.exception.toString()
                    )
                }

                else -> _searchScreenState.update { currentState ->
                    currentState.copy(isLoading = true, error = null)
                }
            }
        }
    }

    private fun saveFavoriteLocation(favoriteLocation: FavoriteLocation) {
        setLoading()
        viewModelScope.launch {
            when (val result = weatherUseCases.saveFavoriteLocation.invoke(favoriteLocation)) {
                is Result.Error -> _searchScreenState.update { currentState ->
                    currentState.copy(
                        addToFavoriteResult = 0,
                        isLoading = false,
                        error = result.exception.toString()
                    )
                }

                is Result.Loading -> _searchScreenState.update { currentState ->
                    currentState.copy(isLoading = true, error = null, addToFavoriteResult = null)
                }

                is Result.Success -> if (result.data != null) {
                    Log.d("TAG", "saveFavoriteLocation: ${result.data}")
                    _searchScreenState.update { currentState ->
                        currentState.copy(
                            addToFavoriteResult = result.data[0].toInt(),
                            isLoading = false,
                            error = null
                        )
                    }
                }

                null -> TODO()
            }
        }
    }

    private fun setLoading() {
        _searchScreenState.update { currentSate ->
            currentSate.copy(isLoading = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
    }

}