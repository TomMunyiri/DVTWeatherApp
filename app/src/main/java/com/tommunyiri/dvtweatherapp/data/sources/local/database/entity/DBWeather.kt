package com.tommunyiri.dvtweatherapp.data.sources.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tommunyiri.dvtweatherapp.domain.model.NetworkSys
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCondition
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCoordinates
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.domain.model.Wind

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@Entity(tableName = "weather_table")
data class DBWeather(
    @ColumnInfo(name = "unique_id")
    @PrimaryKey(autoGenerate = true)
    var uId: Int = 0,
    @ColumnInfo(name = "city_id")
    val cityId: Int,
    @ColumnInfo(name = "city_name")
    val cityName: String,
    @Embedded
    val wind: Wind,
    @ColumnInfo(name = "weather_details")
    val networkWeatherDescription: List<NetworkWeatherDescription>,
    @Embedded
    val networkWeatherCondition: NetworkWeatherCondition,
    @Embedded
    val networkWeatherCoordinates: NetworkWeatherCoordinates,
    @Embedded
    val networkSys: NetworkSys,
)
