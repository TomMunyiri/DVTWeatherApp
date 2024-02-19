package com.tommunyiri.dvtweatherapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCondition
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.model.Wind
import com.tommunyiri.dvtweatherapp.presentation.composables.WeatherForecastItem
import com.tommunyiri.dvtweatherapp.ui.theme.cloudy
import com.tommunyiri.dvtweatherapp.ui.theme.rainy
import com.tommunyiri.dvtweatherapp.ui.theme.sunny
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.utils.convertCelsiusToFahrenheit
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


/**
 * Composable function that represents the home screen of the application.
 */
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val prefs = viewModel.getSharedPrefs()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    state.weather?.let { weather ->
        val weatherForecast1 = WeatherForecast(
            1, "Tue, 9:23PM", Wind(12.12, 123),
            listOf(NetworkWeatherDescription(1L, "main", "Sun", "")),
            NetworkWeatherCondition(42.2, 13.5, 45.3, 12.6, 4.12)
        )
        val weatherForecast2 = WeatherForecast(
            2, "Wed, 9:23PM", Wind(12.12, 123),
            listOf(NetworkWeatherDescription(1L, "main", "Rain", "")),
            NetworkWeatherCondition(56.2, 13.5, 45.3, 12.6, 4.12)
        )
        val weatherForecast3 = WeatherForecast(
            3, "Thur, 9:23PM", Wind(12.12, 123),
            listOf(NetworkWeatherDescription(1L, "main", "Clear", "")),
            NetworkWeatherCondition(12.2, 13.5, 45.3, 12.6, 4.12)
        )
        val weatherForecast4 = WeatherForecast(
            4, "Fri, 9:23PM", Wind(12.12, 123),
            listOf(NetworkWeatherDescription(1L, "main", "Cloudy", "")),
            NetworkWeatherCondition(21.2, 13.5, 45.3, 12.6, 4.12)
        )
        val weatherForecastList =
            listOf(weatherForecast1, weatherForecast2, weatherForecast3, weatherForecast4)

        val screenBackground = when (weather.networkWeatherCondition.toString()) {
            "rainy" -> rainy
            "sunny" -> sunny
            else -> cloudy
        }
        val swipeRefreshState = rememberSwipeRefreshState(
            isRefreshing = viewModel.state.isLoading
        )
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                viewModel.onEvent(HomeScreenEvent.Refresh)
            }
        ) {
            Column(
                modifier = Modifier
                    .background(screenBackground)
                    .fillMaxSize()
            ) {
                TopHeader(weather, prefs, viewModel)
                TempSection(weather, prefs)
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.White)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    items(weatherForecastList.size) { i ->
                        val weatherForecast = weatherForecastList[i]
                        WeatherForecastItem(
                            weatherForecast = weatherForecast,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    /*navigator.navigate(
                                    CompanyInfoScreenDestination(company.symbol)
                                )*/
                                }
                                .padding(0.dp, 10.dp, 0.dp, 10.dp)
                        )
                    }
                }

            }
        }
    }
    if (state.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.error,
            )
        }
    }

}

@Preview
@Composable
fun homeScreenPreview() {
    HomeScreen()
}

@Composable
fun TempSection(weather: Weather?, prefs: SharedPreferenceHelper) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = if (prefs.getSelectedTemperatureUnit() == stringResource(R.string.temp_unit_fahrenheit)) weather?.networkWeatherCondition?.tempMin?.let {
                convertCelsiusToFahrenheit(it)
            }
                .toString() + stringResource(R.string.temp_symbol_fahrenheit) + stringResource(R.string.min) else
                weather?.networkWeatherCondition?.tempMin.toString() + stringResource(R.string.temp_symbol_celsius) + stringResource(
                    R.string.min
                ),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Start,
        )
        Text(
            text = if (prefs.getSelectedTemperatureUnit() == stringResource(R.string.temp_unit_fahrenheit)) weather?.networkWeatherCondition?.temp?.let {
                convertCelsiusToFahrenheit(it)
            }
                .toString() + stringResource(R.string.temp_symbol_fahrenheit) + stringResource(R.string.current) else
                weather?.networkWeatherCondition?.temp.toString() + stringResource(R.string.temp_symbol_celsius) + stringResource(
                    R.string.current
                ),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (prefs.getSelectedTemperatureUnit() == stringResource(R.string.temp_unit_fahrenheit)) weather?.networkWeatherCondition?.tempMax?.let {
                convertCelsiusToFahrenheit(it)
            }
                .toString() + stringResource(R.string.temp_symbol_fahrenheit) + stringResource(R.string.max) else
                weather?.networkWeatherCondition?.tempMax.toString() + stringResource(R.string.temp_symbol_celsius) + stringResource(
                    R.string.max
                ),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
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
    viewModel: HomeScreenViewModel
) {
    val painterResource = when (weather?.networkWeatherDescription.toString()) {
        "rainy" -> R.drawable.forest_rainy
        "sunny" -> R.drawable.forest_sunny
        else -> R.drawable.forest_cloudy
    }

    weather?.cityId?.let { prefs.saveCityId(it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .paint(
                painter = painterResource(id = painterResource),
                contentScale = ContentScale.FillWidth
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = weather?.name.toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 50.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = viewModel.time,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (prefs.getSelectedTemperatureUnit() == stringResource(R.string.temp_unit_fahrenheit)) weather?.networkWeatherCondition?.temp?.let {
                    convertCelsiusToFahrenheit(it)
                }.toString() + stringResource(R.string.temp_symbol_fahrenheit) else
                    weather?.networkWeatherCondition?.temp.toString() + stringResource(
                        R.string.temp_symbol_celsius
                    ),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = weather?.networkWeatherDescription?.first()?.main.toString().uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}
