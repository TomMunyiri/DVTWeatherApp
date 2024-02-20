package com.tommunyiri.dvtweatherapp.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.utils.WeatherIconGenerator
import com.tommunyiri.dvtweatherapp.utils.convertCelsiusToFahrenheit


/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Composable
fun WeatherBottomSheetContent(weather: Weather, prefs: SharedPreferenceHelper) {
    Box(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 40.dp)) {
        // Sheet content
        Image(
            painter = painterResource(id = R.drawable.favorite_24),
            contentDescription = "",
            alignment = Alignment.TopEnd, modifier = Modifier
                .fillMaxWidth()
                .padding(end = 15.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painterResource(id = R.drawable.ic_cloud),
                contentDescription = "contentDescription",
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 40.dp),
            )
            Image(
                painterResource(id = R.drawable.ic_big_cloud),
                contentDescription = "contentDescription",
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 30.dp),
            )
        }
        Column {
            Image(
                painterResource(
                    id = WeatherIconGenerator.getWeatherIcon(
                        weather.networkWeatherDescription.toString().lowercase()
                    )
                ),
                contentDescription = "contentDescription",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
            )
            Text(
                text = weather.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (prefs.getSelectedTemperatureUnit() == stringResource(R.string.temp_unit_fahrenheit)) weather.networkWeatherCondition.temp.let {
                    convertCelsiusToFahrenheit(it)
                }.toString() + stringResource(R.string.temp_symbol_fahrenheit) else
                    weather.networkWeatherCondition.temp.toString() + stringResource(
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
                text = weather.networkWeatherDescription.first().main.toString().uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp, 0.dp, 0.dp),
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Image(
                        painterResource(id = R.drawable.ic_humidity),
                        contentDescription = "contentDescription",
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                    Text(
                        text = "${stringResource(id = R.string.humidity)}\n${weather.networkWeatherCondition.humidity}${
                            stringResource(
                                id = R.string.humidity_symbol
                            )
                        }",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 16.dp, 0.dp, 0.dp),
                        textAlign = TextAlign.Center,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Image(
                        painterResource(id = R.drawable.ic_pressure),
                        contentDescription = "contentDescription",
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                    Text(
                        text = "${stringResource(id = R.string.pressure)}\n${weather.networkWeatherCondition.pressure}${
                            stringResource(
                                id = R.string.pressure_symbol
                            )
                        }",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 16.dp, 0.dp, 0.dp),
                        textAlign = TextAlign.Center,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Image(
                        painterResource(id = R.drawable.ic_wind),
                        contentDescription = "contentDescription",
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                    Text(
                        text = "${stringResource(id = R.string.wind_speed)}\n${weather.wind.speed}${
                            stringResource(
                                id = R.string.wind_speed_symbol
                            )
                        }",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 16.dp, 0.dp, 0.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}