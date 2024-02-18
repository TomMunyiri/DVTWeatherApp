package com.tommunyiri.dvtweatherapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCondition
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.model.Wind
import com.tommunyiri.dvtweatherapp.presentation.composables.WeatherForecastItem
import com.tommunyiri.dvtweatherapp.presentation.navigation.nav.NavItem
import com.tommunyiri.dvtweatherapp.ui.theme.cloudy
import com.tommunyiri.dvtweatherapp.ui.theme.rainy
import com.tommunyiri.dvtweatherapp.ui.theme.sunny


/**
 * Composable function that represents the home screen of the application.
 */
@Composable
fun HomeScreen() {
    /*Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.home_dashboard),
            style = typography.titleLarge,
            color = md_theme_light_error
        )
    }*/

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

    val weather = "sunny"
    val screenBackground = when (weather) {
        "rainy" -> rainy
        "sunny" -> sunny
        else -> cloudy
    }

    Column(
        modifier = Modifier
            .background(screenBackground)
            .fillMaxSize()
    ) {
        TopHeader()
        TempSection()
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.White)
        /*Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                //.visible(isLoading), // Control visibility based on loading state
            )
            Text(
                text = stringResource(id = R.string.loading_text),
                modifier = Modifier
                    .align(Alignment.Center)
                //.visible(isLoading), // Control visibility based on loading state
            )
        }*/
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
                        .padding(0.dp, 20.dp, 0.dp, 0.dp)
                )
            }
        }
    }

}

@Preview
@Composable
fun homeScreenPreview() {
    HomeScreen()
}

@Composable
fun TempSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.temp_min_placeholder),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Start,
        )
        Text(
            text = stringResource(id = R.string.temp_current_placeholder),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.temp_max_placeholder),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp, 0.dp, 0.dp, 0.dp),
            textAlign = TextAlign.Right,
        )
    }
}

@Composable
fun TopHeader() {
    val weather = "rainyty"
    val painterResource = when (weather) {
        "rainy" -> R.drawable.forest_rainy
        "sunny" -> R.drawable.forest_sunny
        else -> R.drawable.forest_cloudy
    }
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
                text = "Kampala District",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 60.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Tuesday Jan 23, 9:21 PM",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(id = R.string.temp_placeholder),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = (stringResource(id = R.string.current_weather_placeholder).uppercase()),
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