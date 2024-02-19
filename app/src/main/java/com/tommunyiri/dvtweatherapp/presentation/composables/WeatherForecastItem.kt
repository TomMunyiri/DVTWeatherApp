package com.tommunyiri.dvtweatherapp.presentation.composables

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCondition
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.model.Wind
import com.tommunyiri.dvtweatherapp.utils.SharedPreferenceHelper


/**
 * Created by Tom Munyiri on 18/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Composable
fun WeatherForecastItem(
    weatherForecast: WeatherForecast,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            ) {
                Text(
                    text = weatherForecast.date,
                    fontSize = 16.sp,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painterResource(
                            id = getWeatherIcon(
                                weatherForecast.networkWeatherCondition.toString().lowercase()
                            )
                        ),
                        contentDescription = "contentDescription",
                        modifier = Modifier,
                    )
                }
                Text(
                    text = getFormattedTemperature(
                        weatherForecast.networkWeatherCondition.temp,
                        LocalContext.current
                    ),
                    fontSize = 16.sp,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

//@Preview()
@Composable
fun WeatherForecastItemPreview() {
    val weatherForecast = WeatherForecast(
        1, "Tue, 9:23PM", Wind(12.12, 123),
        listOf(NetworkWeatherDescription(1L, "main", "Rainy", "")),
        NetworkWeatherCondition(12.2, 13.5, 45.3, 12.6, 4.12)
    )
    WeatherForecastItem(weatherForecast = weatherForecast)
}

fun getFormattedTemperature(double: Double, context: Context): String {
    return if (SharedPreferenceHelper.getInstance(context)
            .getSelectedTemperatureUnit() == context.getString(
            R.string.temp_unit_fahrenheit
        )
    )
        double.toString() + context.resources.getString(R.string.temp_symbol_fahrenheit)
    else
        double.toString() + context.resources.getString(R.string.temp_symbol_celsius)
}

fun getWeatherIcon(condition: String): Int {
    return when (condition.lowercase()) {
        in listOf("sun", "wind", "sunny") -> R.drawable.clear_3x
        in listOf("cloudy", "fog", "overcast") -> R.drawable.partlysunny_3x
        in listOf("rain", "storm", "snow", "blizzard", "thunder") -> R.drawable.rain_3x
        else -> R.drawable.partlysunny_3x // Default icon for other cases
    }
}