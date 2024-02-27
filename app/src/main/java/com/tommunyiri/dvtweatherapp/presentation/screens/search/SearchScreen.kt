package com.tommunyiri.dvtweatherapp.presentation.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.algolia.instantsearch.android.paging3.flow
import com.algolia.instantsearch.compose.searchbox.SearchBoxState
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.presentation.composables.LoadingIndicator
import com.tommunyiri.dvtweatherapp.presentation.composables.WeatherBottomSheetContent
import com.tommunyiri.dvtweatherapp.presentation.utils.WeatherUtils.Companion.getBackgroundColor
import kotlinx.coroutines.launch

/**
 * Composable function that represents the search screen of the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchScreenViewModel = hiltViewModel()) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val paginator = viewModel.hitsPaginator
    val searchBoxState = viewModel.searchBoxState
    val scope = rememberCoroutineScope()
    val pagingHits = paginator.flow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val statsText = viewModel.statsText
    val state by viewModel.searchScreenState.collectAsStateWithLifecycle()
    val prefs = viewModel.getSharedPrefs()


    if (state.isLoading || pagingHits.itemCount == 0) {
        LoadingIndicator()
    }
    Column(modifier = Modifier.padding(top = 45.dp, bottom = 80.dp)) {
        SearchBox(
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(),
            searchBoxState = searchBoxState,
            onValueChange = { scope.launch { listState.scrollToItem(0) } },
        )
        Stats(
            modifier = Modifier.padding(
                start = 15.dp,
                end = 15.dp,
                top = 5.dp,
                bottom = 5.dp
            ),
            stats = statsText.stats
        )

        LazyColumn(modifier = Modifier.fillMaxSize(), listState) {
            items(pagingHits.itemCount) { item ->
                val searchItem = pagingHits[item]
                if (searchItem != null) {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillMaxWidth()
                            .padding(14.dp)
                            .clickable {
                                viewModel.apply {
                                    viewModel.onEvent(SearchScreenEvent.ResetWeather)
                                    onEvent(
                                        SearchScreenEvent.GetWeather(
                                            searchItem.name
                                        )
                                    )
                                }
                            },
                        text = "${searchItem.name}, ${searchItem.country}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp)
                )
            }
        }

        state.weather?.let { weather ->
            val bottomSheetBackgroundColor = getBackgroundColor(weather)
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.onEvent(SearchScreenEvent.ResetWeather)
                },
                sheetState = sheetState,
                containerColor = bottomSheetBackgroundColor
            ) {
                WeatherBottomSheetContent(weather = weather, prefs = prefs, onFavoriteClicked = {
                    viewModel.onEvent(
                        SearchScreenEvent.AddToFavorite(
                            FavoriteLocation(
                                it.name, it.networkWeatherCoordinates.lat,
                                it.networkWeatherCoordinates.lon, it.networkSys.country
                            )
                        )
                    )
                })
            }
        }
    }
}

@Composable
fun SearchBox(
    modifier: Modifier = Modifier, searchBoxState: SearchBoxState = SearchBoxState(),
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier,
        // set query as text value
        value = searchBoxState.query,
        // update text on value change
        onValueChange = {
            searchBoxState.setText(it)
            onValueChange(it)
        },
        // set ime action to "search"
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        // set text as query submit on search action
        keyboardActions = KeyboardActions(
            onSearch = { searchBoxState.setText(searchBoxState.query, true) }
        ),
        placeholder = {
            Text(text = stringResource(id = R.string.enter_city_text))
        },
        maxLines = 1,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        }
    )
}

@Composable
fun Stats(modifier: Modifier = Modifier, stats: String) {
    Text(
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
        text = stats,
        style = MaterialTheme.typography.titleSmall,
        maxLines = 1
    )
}