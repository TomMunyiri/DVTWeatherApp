package com.tommunyiri.dvtweatherapp.data.sources.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCondition
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.domain.model.Wind

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@Entity(tableName = "weather_forecast")
data class DBWeatherForecast(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    @Embedded
    val wind: Wind,
    @ColumnInfo(name = "weather_description")
    val networkWeatherDescriptions: List<NetworkWeatherDescription>,
    @Embedded
    val networkWeatherCondition: NetworkWeatherCondition,
)
