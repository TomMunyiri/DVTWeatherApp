package com.tommunyiri.dvtweatherapp.utils

import android.content.Context
import android.widget.ImageView
import com.tommunyiri.dvtweatherapp.R


/**
 * Created by Tom Munyiri on 20/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherIconGenerator {
    companion object {
        /**
         * This function helps to dynamically set the [WeatherIconView] depending on the weather
         * condition [condition] received.
         * @param iconView the [WeatherIconView] whose icon is to be set
         * @param condition the weather condition
         */
        fun getIconResources(context: Context, iconView: ImageView, condition: String?) {
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
    }
}