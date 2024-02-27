package com.tommunyiri.dvtweatherapp.presentation.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.FavoriteLocation
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.presentation.composables.LoadingIndicator
import com.tommunyiri.dvtweatherapp.presentation.composables.ScreenTitle
import com.tommunyiri.dvtweatherapp.presentation.composables.SweetToast
import com.tommunyiri.dvtweatherapp.presentation.composables.WeatherBottomSheetContent
import com.tommunyiri.dvtweatherapp.presentation.utils.WeatherUtils.Companion.getBackgroundColor

/**
 * Composable function that represents the favorites list screen of the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesScreenViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val state by viewModel.favoritesScreenState.collectAsStateWithLifecycle()
    val currentLocation by viewModel.location.collectAsStateWithLifecycle()
    var lifecycleEvent by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    val prefs = viewModel.getSharedPrefs()

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var openDialogSuccess by remember { mutableStateOf(false) }
    var openDialogError by remember { mutableStateOf(false) }
    var openDialogErrorState by remember { mutableStateOf(false) }

    var showMap by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                lifecycleEvent = event
                viewModel.onEvent(FavoritesScreenEvent.GetFavorites)
            } else if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_DESTROY) {
                viewModel.onEvent(FavoritesScreenEvent.ResetDeleteFavoriteResult)
                viewModel.onEvent(FavoritesScreenEvent.ClearError)
                openDialogError = false
                openDialogSuccess = false
                openDialogErrorState = false
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
        }
    }

    if (openDialogSuccess) {
        openDialogSuccess = false
        SweetToast(text = stringResource(id = R.string.removed_from_favorites), success = true)
    }
    if (openDialogError) {
        openDialogError = false
        SweetToast(
            text = stringResource(id = R.string.error_removing_from_favorites),
            success = false
        )
    }

    if (state.error != null) {
        openDialogErrorState = true
        SweetToast(text = state.error.toString(), success = false)
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = { showMap = !showMap },
            modifier = Modifier.padding(bottom = 80.dp)
        ) {
            if (showMap)
                Icon(Icons.AutoMirrored.Filled.ViewList, contentDescription = "List")
            else
                Icon(Icons.Default.Map, contentDescription = "Map")
        }
    }) { contentPadding ->
        if (state.isLoading) {
            LoadingIndicator()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            ScreenTitle(
                text = stringResource(id = R.string.favorite),
                Modifier.padding(top = 15.dp, start = 15.dp, end = 20.dp)
            )
            state.favoriteLocationsList?.let { favoriteLocationsList ->
                when {
                    favoriteLocationsList.isNotEmpty() ->
                        if (showMap) {
                            FavoriteLocationsMap(favoriteLocationsList, viewModel, currentLocation)
                        } else {
                            FavoriteLocationsList(
                                favoriteLocationsList = favoriteLocationsList,
                                viewModel = viewModel
                            )
                        }

                    else ->
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
            val bottomSheetBackgroundColor = getBackgroundColor(weather)
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    viewModel.onEvent(FavoritesScreenEvent.ResetWeather)
                },
                sheetState = sheetState,
                containerColor = bottomSheetBackgroundColor
            ) {
                WeatherBottomSheetContent(weather = weather, prefs = prefs, onFavoriteClicked = {
                    viewModel.onEvent(FavoritesScreenEvent.RemoveFromFavorite(it.name))
                    showBottomSheet = false
                }, false)
            }
        }
        state.deleteFavoriteResult?.let {
            when (it) {
                1 -> {
                    showBottomSheet = false
                    openDialogSuccess = true
                    viewModel.onEvent(FavoritesScreenEvent.ResetDeleteFavoriteResult)
                }

                0 -> {
                    openDialogError = true
                    viewModel.onEvent(FavoritesScreenEvent.ResetDeleteFavoriteResult)
                }
            }
        }
    }
}

@Composable
fun FavoriteLocationsList(
    favoriteLocationsList: List<FavoriteLocation>,
    viewModel: FavoritesScreenViewModel
) {
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
                            onEvent(FavoritesScreenEvent.ResetWeather)
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
}

@Composable
fun FavoriteLocationsMap(
    favoriteLocationsList: List<FavoriteLocation>,
    viewModel: FavoritesScreenViewModel, currentLocation: LocationModel
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(currentLocation.latitude, currentLocation.longitude), 3f
        )
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp, end = 0.dp, top = 10.dp, bottom = 80.dp),
        cameraPositionState = cameraPositionState
    ) {
        favoriteLocationsList.forEach { favoriteLocation ->
            val markerState = rememberMarkerState(
                position = LatLng(favoriteLocation.lat, favoriteLocation.lon)
            )
            Marker(
                state = markerState,
                title = "${favoriteLocation.name}, ${favoriteLocation.country}",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                onInfoWindowClick = {
                    viewModel.apply {
                        onEvent(FavoritesScreenEvent.ResetWeather)
                        onEvent(FavoritesScreenEvent.GetWeather(favoriteLocation.name))
                    }
                }
            )
        }
    }
}
