package com.tommunyiri.dvtweatherapp.data.sources.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBFavoriteLocation
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeatherForecast

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(vararg dbWeather: DBWeather)

    @Query("SELECT * FROM weather_table ORDER BY unique_id DESC LIMIT 1")
    suspend fun getWeather(): DBWeather

    @Query("SELECT * FROM weather_table ORDER BY unique_id DESC")
    suspend fun getAllWeather(): List<DBWeather>

    @Query("DELETE FROM weather_table")
    suspend fun deleteAllWeather()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastWeather(vararg dbWeatherForecast: DBWeatherForecast)

    @Query("SELECT * FROM weather_forecast ORDER BY id")
    suspend fun getAllWeatherForecast(): List<DBWeatherForecast>

    @Query("DELETE FROM weather_forecast")
    suspend fun deleteAllWeatherForecast()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCity(vararg city: DBFavoriteLocation): List<Long>

    @Query("SELECT * FROM favorite_locations_table ORDER BY name ASC")
    suspend fun getAllFavoriteLocations(): List<DBFavoriteLocation>

    @Query("DELETE FROM favorite_locations_table WHERE name=:name")
    suspend fun deleteFavoriteLocation(name: String): Int
}
