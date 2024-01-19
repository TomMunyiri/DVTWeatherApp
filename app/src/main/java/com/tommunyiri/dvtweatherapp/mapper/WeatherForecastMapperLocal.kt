package com.tommunyiri.dvtweatherapp.mapper

import com.tommunyiri.dvtweatherapp.data.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.data.source.local.entity.DBWeatherForecast


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherForecastMapperLocal : BaseMapper<List<DBWeatherForecast>, List<WeatherForecast>> {
    override fun transformToDomain(type: List<DBWeatherForecast>): List<WeatherForecast> {
        return type.map { dbWeatherForecast ->
            WeatherForecast(
                dbWeatherForecast.id,
                dbWeatherForecast.date,
                dbWeatherForecast.wind,
                dbWeatherForecast.networkWeatherDescriptions,
                dbWeatherForecast.networkWeatherCondition
            )
        }
    }

    override fun transformToDto(type: List<WeatherForecast>): List<DBWeatherForecast> {
        return type.map { weatherForecast ->
            DBWeatherForecast(
                weatherForecast.uID,
                weatherForecast.date,
                weatherForecast.wind,
                weatherForecast.networkWeatherDescription,
                weatherForecast.networkWeatherCondition
            )
        }
    }
}