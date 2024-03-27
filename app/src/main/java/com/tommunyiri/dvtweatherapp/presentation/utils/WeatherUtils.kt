package com.tommunyiri.dvtweatherapp.presentation.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.core.ui.theme.cloudy
import com.tommunyiri.dvtweatherapp.core.ui.theme.rainy
import com.tommunyiri.dvtweatherapp.core.ui.theme.sunny
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper
import com.tommunyiri.dvtweatherapp.domain.model.Weather

/**
 * Created by Tom Munyiri on 20/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherUtils {
    companion object {
        /**
         * This function helps to dynamically get the [Int] drawable depending on the weather
         * condition [condition] received.
         * @param condition the weather condition
         */

        fun getWeatherIcon(condition: String): Int {
            return when {
                condition.contains("sun", true) ||
                    condition.contains("wind", true) -> R.drawable.clear_3x

                condition.contains("cloudy", true) ||
                    condition.contains("fog", true) ||
                    condition.contains("overcast", true) -> R.drawable.partlysunny_3x

                condition.contains("rain", true) ||
                    condition.contains("storm", true) ||
                    condition.contains("snow", true) ||
                    condition.contains("blizzard", true) ||
                    condition.contains("thunder", true) -> R.drawable.rain_3x

                else -> R.drawable.partlysunny_3x // Default icon for other cases
            }
        }

        fun getBackgroundColor(weather: Weather): Color {
            return when {
                weather.networkWeatherDescription.toString()
                    .contains("cloud", true) -> cloudy

                weather.networkWeatherDescription.toString().contains("rain", true) ||
                    weather.networkWeatherDescription.toString().contains("snow", true) ||
                    weather.networkWeatherDescription.toString().contains("mist", true) ||
                    weather.networkWeatherDescription.toString()
                        .contains("haze", true) -> rainy

                else -> sunny
            }
        }

        fun getBackgroundImage(weather: Weather?): Int {
            return when {
                weather?.networkWeatherDescription.toString()
                    .contains("cloud", true) -> R.drawable.forest_cloudy

                weather?.networkWeatherDescription.toString().contains("rain", true) ||
                    weather?.networkWeatherDescription.toString().contains("snow", true) ||
                    weather?.networkWeatherDescription.toString().contains("mist", true) ||
                    weather?.networkWeatherDescription.toString()
                        .contains("haze", true) -> R.drawable.forest_rainy

                else -> R.drawable.forest_sunny
            }
        }

        fun getFormattedTemperature(
            prefs: SharedPreferenceHelper,
            weather: Weather?,
            context: Context,
        ): String {
            return if (prefs.getSelectedTemperatureUnit() == context.getString(R.string.temp_unit_fahrenheit)) {
                weather?.networkWeatherCondition?.temp?.let {
                    convertCelsiusToFahrenheit(it)
                }.toString() + context.getString(R.string.temp_symbol_fahrenheit)
            } else {
                weather?.networkWeatherCondition?.temp.toString() + context.getString(R.string.temp_symbol_celsius)
            }
        }

        fun getFormattedTemperature(
            double: Double,
            prefs: SharedPreferenceHelper,
            context: Context,
        ): String {
            return if (
                prefs.getSelectedTemperatureUnit() ==
                context.getString(
                    R.string.temp_unit_fahrenheit,
                )
            ) {
                double.toString() + context.resources.getString(R.string.temp_symbol_fahrenheit)
            } else {
                double.toString() + context.resources.getString(R.string.temp_symbol_celsius)
            }
        }
    }
}
