package com.tommunyiri.dvtweatherapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.presentation.components.LoadingIndicator
import com.tommunyiri.dvtweatherapp.presentation.components.SweetToast
import com.tommunyiri.dvtweatherapp.presentation.components.WeatherForecastItem
import com.tommunyiri.dvtweatherapp.presentation.utils.WeatherUtils.Companion.getBackgroundColor
import com.tommunyiri.dvtweatherapp.presentation.utils.WeatherUtils.Companion.getBackgroundImage
import com.tommunyiri.dvtweatherapp.presentation.utils.WeatherUtils.Companion.getFormattedTemperature

/**
 * Composable function that represents the home screen of the application.
 */
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val state by viewModel.homeScreenState.collectAsStateWithLifecycle()
    val prefs = viewModel.prefs
    var openDialogError by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_DESTROY) {
                    viewModel.onEvent(HomeScreenEvent.ClearError)
                    openDialogError = false
                }
            }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
    if (state.isLoading) {
        LoadingIndicator()
    }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isRefreshing)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            viewModel.onEvent(HomeScreenEvent.Refresh)
        },
    ) {
        state.weather?.let { weather ->
            val screenBackgroundColor = getBackgroundColor(weather)
            prefs.saveCityId(weather.cityId)
            Scaffold { innerPadding ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(screenBackgroundColor)
                            .padding(innerPadding),
                ) {
                    TopHeader(weather, prefs, viewModel)
                    TempSection(weather, prefs)
                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.White)
                    if (state.isLoadingForecast) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(30.dp),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.weatherForecastList?.let { weatherForecastList ->
                        LazyColumn(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp, 16.dp, 16.dp, 80.dp),
                        ) {
                            items(weatherForecastList.size) { i ->
                                val weatherForecast = weatherForecastList[i]
                                WeatherForecastItem(
                                    weatherForecast = weatherForecast,
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(0.dp, 10.dp, 0.dp, 10.dp),
                                    prefs,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (state.error != null && !openDialogError) {
        openDialogError = true
        SweetToast(text = state.error.toString(), success = false)
    }
}

@Composable
fun TempSection(
    weather: Weather?,
    prefs: SharedPreferenceHelper,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text =
                getFormattedTemperature(
                    prefs,
                    weather,
                    LocalContext.current,
                ) + stringResource(id = R.string.min),
            color = Color.White,
            fontSize = 20.sp,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Start,
        )
        Text(
            text =
                getFormattedTemperature(
                    prefs,
                    weather,
                    LocalContext.current,
                ) + stringResource(id = R.string.current),
            color = Color.White,
            fontSize = 20.sp,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text =
                getFormattedTemperature(
                    prefs,
                    weather,
                    LocalContext.current,
                ) + stringResource(id = R.string.max),
            color = Color.White,
            fontSize = 20.sp,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Right,
        )
    }
}

@Composable
fun TopHeader(
    weather: Weather?,
    prefs: SharedPreferenceHelper,
    viewModel: HomeScreenViewModel,
) {
    val painterResource = getBackgroundImage(weather)

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .paint(
                    painter = painterResource(id = painterResource),
                    contentScale = ContentScale.FillWidth,
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(16.dp),
        ) {
            Text(
                text = weather?.name.toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = viewModel.time,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = getFormattedTemperature(prefs, weather, LocalContext.current),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 16.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = weather?.networkWeatherDescription?.first()?.main.toString().uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 16.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}
