package com.tommunyiri.dvtweatherapp.data.mappers

import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeather


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class WeatherMapperLocal : BaseMapper<DBWeather, Weather> {
    override fun transformToDomain(type: DBWeather): Weather = Weather(
        uId = type.uId,
        cityId = type.cityId,
        name = type.cityName,
        wind = type.wind,
        networkWeatherDescription = type.networkWeatherDescription,
        networkWeatherCondition = type.networkWeatherCondition,
        networkWeatherCoordinates = type.networkWeatherCoordinates,
        networkSys = type.networkSys
    )

    override fun transformToDto(type: Weather): DBWeather = DBWeather(
        uId = type.uId,
        cityId = type.cityId,
        cityName = type.name,
        wind = type.wind,
        networkWeatherDescription = type.networkWeatherDescription,
        networkWeatherCondition = type.networkWeatherCondition,
        networkWeatherCoordinates = type.networkWeatherCoordinates,
        networkSys = type.networkSys
    )
}