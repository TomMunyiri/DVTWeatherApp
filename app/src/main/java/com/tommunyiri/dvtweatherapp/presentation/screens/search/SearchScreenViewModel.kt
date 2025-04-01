package com.tommunyiri.dvtweatherapp.presentation.screens.search

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
import com.tommunyiri.dvtweatherapp.data.repository.LocationRepository
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.SearchResult
import com.tommunyiri.dvtweatherapp.domain.usecases.preferences.GetSharedPreferencesUseCase
import com.tommunyiri.dvtweatherapp.domain.usecases.weather.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@HiltViewModel
class SearchScreenViewModel
@Inject
constructor(
    private val locationRepository: LocationRepository,
    private val weatherUseCases: WeatherUseCases,
    private val getPrefsUseCase: GetSharedPreferencesUseCase,
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow(SearchScreenState())
    val searchScreenState: StateFlow<SearchScreenState> = _searchScreenState.asStateFlow()

    private val _location = MutableStateFlow(LocationModel(0.00, 0.00))
    val location: StateFlow<LocationModel> = _location.asStateFlow()

    private val applicationID = BuildConfig.ALGOLIA_APP_ID
    private val algoliaAPIKey = BuildConfig.ALGOLIA_API_KEY
    private val algoliaIndexName = BuildConfig.ALGOLIA_INDEX_NAME

    private val searcher =
        HitsSearcher(
            applicationID = ApplicationID(applicationID),
            apiKey = APIKey(algoliaAPIKey),
            indexName = IndexName(algoliaIndexName),
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
        getCurrentLocation()
    }

    fun onEvent(event: SearchScreenEvent) {
        when (event) {
            is SearchScreenEvent.GetWeather -> {
                getSearchWeather(event.city)
            }

            is SearchScreenEvent.ResetWeather ->
                _searchScreenState.update { currentState ->
                    currentState.copy(weather = null)
                }

            is SearchScreenEvent.ClearError ->
                _searchScreenState.update { currentState ->
                    currentState.copy(error = null)
                }

            is SearchScreenEvent.AddToFavorite -> {
                saveFavoriteLocation(event.favoriteLocation)
            }

            is SearchScreenEvent.ResetAddToFavoriteResult -> _searchScreenState.update { currentState ->
                currentState.copy(addToFavoriteResult = null)
            }
        }
    }

    private fun getSearchWeather(name: String) {
        setLoading()
        viewModelScope.launch {
            weatherUseCases.getSearchWeather.invoke(name)
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data != null) {
                                _searchScreenState.update { currentState ->
                                    currentState.copy(
                                        weather = result.data,
                                        isLoading = false,
                                        error = null,
                                    )
                                }
                            } else {
                                _searchScreenState.update { currentState ->
                                    currentState.copy(
                                        weather = null,
                                        isLoading = false,
                                        error = "No weather data at the moment",
                                    )
                                }
                            }
                        }

                        is Result.Error ->
                            _searchScreenState.update { currentState ->
                                currentState.copy(
                                    weather = null,
                                    isLoading = false,
                                    error = result.exception.toString(),
                                )
                            }

                        is Result.Loading ->
                            _searchScreenState.update { currentState ->
                                currentState.copy(isLoading = true, error = null)
                            }
                    }
                }
                .catch { error ->
                    _searchScreenState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .launchIn(this)
        }
    }

    private fun saveFavoriteLocation(name: FavoriteLocation) {
        setLoading()
        viewModelScope.launch {
            weatherUseCases.saveFavoriteLocation.invoke(name)
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data != null) {
                                _searchScreenState.update { currentState ->
                                    currentState.copy(
                                        addToFavoriteResult = result.data.first(),
                                        isLoading = false,
                                        error = null,
                                    )
                                }
                            }
                        }

                        is Result.Loading ->
                            _searchScreenState.update { currentState ->
                                currentState.copy(
                                    isLoading = true,
                                    error = null,
                                    addToFavoriteResult = null
                                )
                            }

                        is Result.Error ->
                            _searchScreenState.update { currentState ->
                                currentState.copy(
                                    addToFavoriteResult = 0,
                                    weather = null,
                                    isLoading = false,
                                    error = result.exception.toString(),
                                )
                            }
                    }
                }
                .catch { error ->
                    _searchScreenState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .launchIn(this)
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
        locationRepository.stopLocationUpdates()
    }

    fun getSharedPrefs(): SharedPreferenceHelper {
        return getPrefsUseCase()
    }

    private fun getCurrentLocation() {
        locationRepository.startLocationUpdates()
        locationRepository.locationStateFlow
            .onEach { locationValue ->
                if (locationValue != null) {
                    _location.update { locationValue }
                }
            }
            .catch { error ->
                // Handle location error if needed
            }
            .launchIn(viewModelScope)
    }
}
