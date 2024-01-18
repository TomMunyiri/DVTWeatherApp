package com.tommunyiri.dvtweatherapp.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tommunyiri.dvtweatherapp.data.source.local.dao.WeatherDao
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeatherForecast
import com.tommunyiri.dvtweatherapp.utils.typeconverters.ListNetworkWeatherDescriptionConverter


/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@Database(entities = [DBWeather::class, DBWeatherForecast::class], version = 1, exportSchema = true)
@TypeConverters(
    ListNetworkWeatherDescriptionConverter::class
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val weatherDao: WeatherDao
}