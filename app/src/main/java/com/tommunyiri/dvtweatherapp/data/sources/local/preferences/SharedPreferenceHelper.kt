package com.tommunyiri.dvtweatherapp.data.sources.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.tommunyiri.dvtweatherapp.domain.model.LocationModel

/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class SharedPreferenceHelper {
    companion object {
        private const val WEATHER_PREF_TIME = "Weather pref time"
        private const val WEATHER_FORECAST_PREF_TIME = "Forecast pref time"
        private const val CITY_ID = "City ID"
        private var prefs: SharedPreferences? = null
        private const val LOCATION = "LOCATION"

        @Volatile
        private var instance: SharedPreferenceHelper? = null

        /**
         * This checks if there is an existing instance of the [SharedPreferences] in the
         * specified [context] and creates one if there isn't or else, it returns the
         * already existing instance. This function ensures that the [SharedPreferences] is
         * accessed at any instance by a single thread.
         */
        fun getInstance(context: Context): SharedPreferenceHelper {
            synchronized(this) {
                val newInstance = instance
                if (newInstance == null) {
                    prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    instance = newInstance
                }
                return SharedPreferenceHelper()
            }
        }
    }

    /**
     * This function saves the initial time [System.nanoTime] at which the weather information
     * at the user's location is accessed.
     * @param time the value of [System.nanoTime] when the weather information is received.
     */
    private fun saveTimeOfInitialWeatherFetch(time: Long) {
        prefs?.edit(commit = true) {
            putLong(WEATHER_PREF_TIME, time)
        }
    }

    /**
     * This function returns the saved value of [System.nanoTime] when the weather information
     * at the user's location was accessed.
     * @see saveTimeOfInitialWeatherFetch
     */
    fun getTimeOfInitialWeatherFetch() = prefs?.getLong(WEATHER_PREF_TIME, 0L)

    /**
     * This function saves the initial time [System.nanoTime] at which the weather forecast
     * at the user's location is accessed.
     * @param time the value of [System.nanoTime] when the weather forecast is received.
     */
    private fun saveTimeOfInitialWeatherForecastFetch(time: Long) {
        prefs?.edit(commit = true) {
            putLong(WEATHER_FORECAST_PREF_TIME, time)
        }
    }

    /**
     * This function returns the saved value of [System.nanoTime] when the weather forecast
     * at the user's location was accessed.
     * @see saveTimeOfInitialWeatherForecastFetch
     */
    fun getTimeOfInitialWeatherForecastFetch() = prefs?.getLong(WEATHER_FORECAST_PREF_TIME, 0L)

    /**
     * This function saves the [cityId] of the location whose weather information has been
     * received.
     * @param cityId the id of the location whose weather has been received
     */
    fun saveCityId(cityId: Int) {
        prefs?.edit(commit = true) {
            putInt(CITY_ID, cityId)
        }
    }

    /**
     * This function returns the id of the location whose weather information has been received.
     * @see saveCityId
     */
    fun getCityId() = prefs?.getInt(CITY_ID, 0)

    /**
     * This function gets the value of the cache duration the user set in the
     * Settings.
     */
    fun getUserSetCacheDuration() = prefs?.getString("cache_key", "5")

    /**
     * This function saves a [Temp Unit]
     */
    fun saveCacheDuration(cacheDuration: String) {
        prefs?.edit(commit = true) {
            putString("cache_key", cacheDuration)
        }
    }

    /**
     * This function gets the value of the app theme the user set in the
     * Settings.
     */
    fun getSelectedThemePref() = prefs?.getString("theme_key", "Light")

    /**
     * This function saves a [Temp Unit]
     */
    fun saveThemePref(theme: String) {
        prefs?.edit(commit = true) {
            putString("theme_key", theme)
        }
    }

    /**
     * This function gets the value of the temperature unit the user set in the
     * Settings.
     */
    fun getSelectedTemperatureUnit() = prefs?.getString("unit_key", "Celsius/°C")

    /**
     * This function saves a [Temp Unit]
     */
    fun saveTemperatureUnit(temperatureUnit: String) {
        prefs?.edit(commit = true) {
            putString("unit_key", temperatureUnit)
        }
    }

    /**
     * This function saves a [LocationModel]
     */
    fun saveLocation(location: LocationModel) {
        prefs?.edit(commit = true) {
            val gson = Gson()
            val json = gson.toJson(location)
            putString(LOCATION, json)
        }
    }

    /**
     * This function gets the value of the saved [LocationModel]
     */
    fun getLocation(): LocationModel? {
        val gson = Gson()
        val json = prefs?.getString(LOCATION, null)
        return gson.fromJson(json, LocationModel::class.java)
    }
}
