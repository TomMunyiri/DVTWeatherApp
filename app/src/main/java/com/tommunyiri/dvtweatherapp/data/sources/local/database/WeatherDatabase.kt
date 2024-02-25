package com.tommunyiri.dvtweatherapp.data.sources.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tommunyiri.dvtweatherapp.data.sources.local.database.dao.WeatherDao
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBFavoriteLocation
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeatherForecast
import com.tommunyiri.dvtweatherapp.data.sources.local.database.typeconverters.ListNetworkWeatherDescriptionConverter


/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@Database(
    entities = [DBWeather::class, DBWeatherForecast::class, DBFavoriteLocation::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(ListNetworkWeatherDescriptionConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val weatherDao: WeatherDao
}