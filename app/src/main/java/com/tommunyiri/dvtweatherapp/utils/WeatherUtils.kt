package com.tommunyiri.dvtweatherapp.utils

import android.content.Context
import android.widget.ImageView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.ui.theme.cloudy
import com.tommunyiri.dvtweatherapp.ui.theme.rainy
import com.tommunyiri.dvtweatherapp.ui.theme.sunny

/**
 * Created by Tom Munyiri on 20/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherUtils {
    companion object {
        /**
         * This function helps to dynamically set the [ImageView] depending on the weather
         * condition [condition] received.
         * @param iconView the [ImageView] whose icon is to be set
         * @param condition the weather condition
         */
        fun getIconResources(iconView: ImageView, condition: String?) {
            if (condition != null) {
                when {
                    condition.contains("rain", ignoreCase = true)
                            || condition.contains("Storm", ignoreCase = true)
                            || condition.contains("storm", ignoreCase = true)
                            || condition.contains("Rain", ignoreCase = true)
                            || condition.contains("ice", ignoreCase = true)
                            || condition.contains("Ice", ignoreCase = true)
                            || condition.contains("Thunder", ignoreCase = true)
                            || condition.contains("thunderstorm", ignoreCase = true)
                            || condition.contains("thunderstorm", ignoreCase = true)
                            || condition.contains("drizzle", ignoreCase = true)
                            || condition.contains("Mist", ignoreCase = true)
                            || condition.contains("sleet", ignoreCase = true)
                            || condition.contains("snow", ignoreCase = true)
                            || condition.contains("Blizzard", ignoreCase = true)
                            || condition.contains("thunder", ignoreCase = true) -> {
                        iconView.setImageResource(R.drawable.rain_2x)
                    }

                    condition.contains("sun", ignoreCase = true)
                            || condition.contains("wind", ignoreCase = true)
                            || condition.contains("Wind", ignoreCase = true)
                            || condition.contains("Sunny", ignoreCase = true) -> {
                        iconView.setImageResource(R.drawable.clear_2x)
                    }

                    condition.contains("cloud", ignoreCase = true)
                            || condition.contains("Fog", ignoreCase = true)
                            || condition.contains("Cloudy", ignoreCase = true)
                            || condition.contains("Clear", ignoreCase = true)
                            || condition.contains("Overcast", ignoreCase = true) -> {
                        iconView.setImageResource(R.drawable.partlysunny_2x)
                    }

                    else -> {
                        iconView.setImageResource(R.drawable.clear_2x)
                    }
                }
            }
        }

        fun getWeatherIcon(condition: String): Int {
            return when {
                condition.contains("sun", true)
                        || condition.contains("wind", true) -> R.drawable.clear_3x

                condition.contains("cloudy", true)
                        || condition.contains("fog", true)
                        || condition.contains("overcast", true) -> R.drawable.partlysunny_3x

                condition.contains("rain", true)
                        || condition.contains("storm", true)
                        || condition.contains("snow", true)
                        || condition.contains("blizzard", true)
                        || condition.contains("thunder", true) -> R.drawable.rain_3x

                else -> R.drawable.partlysunny_3x // Default icon for other cases

            }
        }

        fun getBackgroundColor(weather: Weather): Color {
            return when {
                weather.networkWeatherDescription.toString()
                    .contains("cloud", true) -> cloudy

                weather.networkWeatherDescription.toString().contains("rain", true) ||
                        weather.networkWeatherDescription.toString().contains("snow", true)
                        || weather.networkWeatherDescription.toString().contains("mist", true)
                        || weather.networkWeatherDescription.toString()
                    .contains("haze", true) -> rainy

                else -> sunny
            }
        }

        fun getBackgroundImage(weather: Weather?): Int {
            return when {
                weather?.networkWeatherDescription.toString()
                    .contains("cloud", true) -> R.drawable.forest_cloudy

                weather?.networkWeatherDescription.toString().contains("rain", true) ||
                        weather?.networkWeatherDescription.toString().contains("snow", true)
                        || weather?.networkWeatherDescription.toString().contains("mist", true)
                        || weather?.networkWeatherDescription.toString()
                    .contains("haze", true) -> R.drawable.forest_rainy

                else -> R.drawable.forest_sunny
            }
        }

        fun getFormattedTemperature(
            prefs: SharedPreferenceHelper, weather: Weather?, context: Context
        ): String {
            return if (prefs.getSelectedTemperatureUnit() == context.getString(R.string.temp_unit_fahrenheit)) weather?.networkWeatherCondition?.temp?.let {
                convertCelsiusToFahrenheit(it)
            }.toString() + context.getString(R.string.temp_symbol_fahrenheit) else
                weather?.networkWeatherCondition?.temp.toString() + context.getString(R.string.temp_symbol_celsius)
        }

        fun getFormattedTemperature(
            double: Double, prefs: SharedPreferenceHelper, context: Context
        ): String {
            return if (
                prefs.getSelectedTemperatureUnit() == context.getString(
                    R.string.temp_unit_fahrenheit
                )
            )
                double.toString() + context.resources.getString(R.string.temp_symbol_fahrenheit)
            else
                double.toString() + context.resources.getString(R.string.temp_symbol_celsius)
        }
    }
}