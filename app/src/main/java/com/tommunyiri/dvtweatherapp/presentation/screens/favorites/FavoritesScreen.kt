package com.tommunyiri.dvtweatherapp.presentation.screens.favorites

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetError
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetSuccess
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.presentation.composables.ScreenTitle
import com.tommunyiri.dvtweatherapp.presentation.composables.WeatherBottomSheetContent
import com.tommunyiri.dvtweatherapp.ui.theme.cloudy
import com.tommunyiri.dvtweatherapp.ui.theme.rainy
import com.tommunyiri.dvtweatherapp.ui.theme.sunny

/**
 * Composable function that represents the list screen of the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesScreenViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val state = viewModel.state
    var lifecycleEvent by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    val prefs = viewModel.getSharedPrefs()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var openDialogSuccess by remember { mutableStateOf(false) }
    var openDialogError by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                lifecycleEvent = event
                viewModel.onEvent(FavoritesScreenEvent.GetFavorites)
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LaunchedEffect(lifecycleEvent) {
        if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(FavoritesScreenEvent.GetFavorites)
            viewModel.state = viewModel.state.copy(deleteFavoriteResult = null)
        }
    }

    Scaffold(
        /*floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show bottom sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }*/
    ) { contentPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle(text = stringResource(id = R.string.favorite))
            state.favoriteLocationsList?.let { favoriteLocationsList ->
                if (favoriteLocationsList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 80.dp),
                    ) {
                        items(favoriteLocationsList.size) { i ->
                            val favoriteLocation = favoriteLocationsList[i]
                            Text(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .fillMaxWidth()
                                    .padding(14.dp)
                                    .clickable {
                                        viewModel.apply {
                                            viewModel.state = state.copy(weather = null)
                                            onEvent(FavoritesScreenEvent.GetWeather(favoriteLocation.name))
                                        }
                                    },
                                text = "${favoriteLocation.name}, ${favoriteLocation.country}",
                                style = typography.bodyMedium
                            )
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .width(1.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.zero_favorites_text),
                            style = typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        state.weather?.let { weather ->
            val bottomSheetBackgroundColor = when {
                weather.networkWeatherDescription.toString()
                    .contains("cloud", true) -> cloudy

                weather.networkWeatherDescription.toString().contains("rain", true) ||
                        weather.networkWeatherDescription.toString().contains("snow", true)
                        || weather.networkWeatherDescription.toString().contains("mist", true)
                        || weather.networkWeatherDescription.toString()
                    .contains("haze", true) -> rainy

                else -> sunny
            }
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.state = state.copy(weather = null)
                },
                sheetState = sheetState,
                containerColor = bottomSheetBackgroundColor
            ) {
                WeatherBottomSheetContent(weather = weather, prefs = prefs, onFavoriteClicked = {
                    viewModel.onEvent(FavoritesScreenEvent.RemoveFromFavorite(it.name))
                    showBottomSheet = false
                })
            }
        }
        state.deleteFavoriteResult?.let {
            if (it == 1) {
                showBottomSheet = false
                openDialogSuccess = true
                viewModel.state = viewModel.state.copy(deleteFavoriteResult = null)
            } else if (it == 0) {
                openDialogError = true
                viewModel.state = viewModel.state.copy(deleteFavoriteResult = null)
            }
        }
    }

    if (openDialogSuccess) {
        openDialogSuccess = false
        SweetSuccess(
            message = "Removed from favorites",
            duration = Toast.LENGTH_SHORT,
            padding = PaddingValues(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        )
    }
    if (openDialogError) {
        openDialogError = false
        SweetError(
            message = "Error removing from favorites",
            duration = Toast.LENGTH_SHORT,
            padding = PaddingValues(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        )
    }
}