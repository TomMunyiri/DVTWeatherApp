package com.tommunyiri.dvtweatherapp.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.presentation.utils.WeatherUtils
import com.tommunyiri.dvtweatherapp.presentation.utils.WeatherUtils.Companion.getFormattedTemperature


/**
 * Created by Tom Munyiri on 20/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Composable
fun WeatherBottomSheetContent(
    weather: Weather, prefs: SharedPreferenceHelper,
    onFavoriteClicked: (weather: Weather) -> Unit, addToFavorite: Boolean
) {
    Box(modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 40.dp)) {
        // Sheet content
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            Image(
                painter = if (addToFavorite) painterResource(id = R.drawable.favorite_24) else painterResource(
                    id = R.drawable.favorite_border_24
                ),
                contentDescription = stringResource(id = R.string.remove_from_favorites),
                alignment = Alignment.TopEnd, modifier = Modifier
                    .padding(end = 15.dp)
                    .clickable { onFavoriteClicked(weather) },
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painterResource(id = R.drawable.ic_cloud),
                contentDescription = stringResource(id = R.string.small_cloud),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 70.dp)
            )
            Image(
                painterResource(id = R.drawable.ic_big_cloud),
                contentDescription = stringResource(id = R.string.big_cloud),
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 30.dp),
            )
        }
        Column {
            Image(
                painterResource(
                    id = WeatherUtils.getWeatherIcon(
                        weather.networkWeatherDescription.toString().lowercase()
                    )
                ),
                contentDescription = stringResource(id = R.string.weather_bottom_sheet_bg_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(50.dp)
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
                text = getFormattedTemperature(prefs, weather, LocalContext.current),
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
                        contentDescription = stringResource(id = R.string.humidity_icon),
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                    Text(
                        text = "${stringResource(id = R.string.humidity)}\n${weather.networkWeatherCondition.humidity}${
                            stringResource(id = R.string.humidity_symbol)
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
                        contentDescription = stringResource(id = R.string.pressure_icon),
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                    Text(
                        text = "${stringResource(id = R.string.pressure)}\n${weather.networkWeatherCondition.pressure}${
                            stringResource(id = R.string.pressure_symbol)
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
                        contentDescription = stringResource(id = R.string.wind_speed_icon),
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                    Text(
                        text = "${stringResource(id = R.string.wind_speed)}\n${weather.wind.speed}${
                            stringResource(id = R.string.wind_speed_symbol)
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