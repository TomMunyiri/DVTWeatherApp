package com.tommunyiri.dvtweatherapp.data.mapper

import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeather
import com.tommunyiri.dvtweatherapp.domain.model.Weather


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherMapperRemote : BaseMapper<NetworkWeather, Weather> {
    override fun transformToDomain(type: NetworkWeather): Weather = Weather(
        uId = type.uId,
        cityId = type.cityId,
        name = type.name,
        wind = type.wind,
        networkWeatherDescription = type.networkWeatherDescriptions,
        networkWeatherCondition = type.networkWeatherCondition,
        networkWeatherCoordinates = type.networkWeatherCoordinates,
        networkSys = type.networkSys
    )

    override fun transformToDto(type: Weather): NetworkWeather = NetworkWeather(
        uId = type.uId,
        cityId = type.cityId,
        name = type.name,
        wind = type.wind,
        networkWeatherDescriptions = type.networkWeatherDescription,
        networkWeatherCondition = type.networkWeatherCondition,
        networkWeatherCoordinates = type.networkWeatherCoordinates,
        networkSys = type.networkSys
    )
}
