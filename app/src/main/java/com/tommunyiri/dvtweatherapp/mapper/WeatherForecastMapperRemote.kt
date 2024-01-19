package com.tommunyiri.dvtweatherapp.mapper

import com.tommunyiri.dvtweatherapp.data.model.NetworkWeatherForecast
import com.tommunyiri.dvtweatherapp.data.model.WeatherForecast


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherForecastMapperRemote :
    BaseMapper<List<NetworkWeatherForecast>, List<WeatherForecast>> {
    override fun transformToDomain(type: List<NetworkWeatherForecast>): List<WeatherForecast> {
        return type.map { networkWeatherForecast ->
            WeatherForecast(
                networkWeatherForecast.id,
                networkWeatherForecast.date,
                networkWeatherForecast.wind,
                networkWeatherForecast.networkWeatherDescription,
                networkWeatherForecast.networkWeatherCondition
            )
        }
    }

    override fun transformToDto(type: List<WeatherForecast>): List<NetworkWeatherForecast> {
        return type.map { weatherForecast ->
            NetworkWeatherForecast(
                weatherForecast.uID,
                weatherForecast.date,
                weatherForecast.wind,
                weatherForecast.networkWeatherDescription,
                weatherForecast.networkWeatherCondition
            )
        }
    }
}