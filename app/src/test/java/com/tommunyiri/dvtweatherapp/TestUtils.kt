package com.tommunyiri.dvtweatherapp

import com.tommunyiri.dvtweatherapp.domain.model.LocationModel
import com.tommunyiri.dvtweatherapp.domain.model.NetworkSys
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeather
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCondition
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherCoordinates
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherDescription
import com.tommunyiri.dvtweatherapp.domain.model.NetworkWeatherForecast
import com.tommunyiri.dvtweatherapp.domain.model.Weather
import com.tommunyiri.dvtweatherapp.domain.model.WeatherForecast
import com.tommunyiri.dvtweatherapp.domain.model.Wind
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeather
import com.tommunyiri.dvtweatherapp.data.sources.local.database.entity.DBWeatherForecast


/**
 * Created by Tom Munyiri on 22/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

val fakeDbWeatherEntity = DBWeather(
    1,
    123,
    "Nairobi",
    Wind(32.5, 24),
    listOf(NetworkWeatherDescription(1L, "Main", "Cloudy", "icon")),
    NetworkWeatherCondition(324.43, 1234.32, 32.5, 34.4, 34.6),
    NetworkWeatherCoordinates(34.5, 4.6),
    NetworkSys("Kenya", 12342, 23444)
)

val fakeDbWeatherForecast = DBWeatherForecast(
    1, "Date", Wind(22.2, 21),
    listOf(
        NetworkWeatherDescription(1L, "Main", "Desc", "Icon")
    ),
    NetworkWeatherCondition(22.3, 22.2, 22.2, 34.4, 34.6)
)

val dummyLocation = LocationModel(12.2, 23.4)

val fakeNetworkWeather = NetworkWeather(
    1,
    123,
    "Nairobi",
    Wind(32.5, 24),
    listOf(NetworkWeatherDescription(1L, "Main", "Cloudy", "icon")),
    NetworkWeatherCondition(324.43, 1234.32, 32.5, 34.4, 34.6),
    NetworkWeatherCoordinates(34.5, 4.6),
    NetworkSys("Kenya", 12342, 23444)
)

val fakeLocationModel = LocationModel(234.45,657.4)

val fakeNetworkWeatherForecast = NetworkWeatherForecast(
    1, "Date", Wind(22.2, 21),
    listOf(
        NetworkWeatherDescription(1L, "Main", "Desc", "Icon")
    ),
    NetworkWeatherCondition(22.3, 22.2, 22.2, 456.2, 34.5)
)

val fakeWeather = Weather(
    1,
    123,
    "Nairobi",
    Wind(32.5, 24),
    listOf(NetworkWeatherDescription(1L, "Main", "Cloudy", "cloud")),
    NetworkWeatherCondition(324.43, 1234.32, 32.5, 45.4, 124.9),
    NetworkWeatherCoordinates(34.5, 4.6),
    NetworkSys("Kenya", 12342, 23444)
)

val fakeWeatherForecast = WeatherForecast(
    1, "2021-07-25 14:22:10", Wind(22.2, 21),
    listOf(
        NetworkWeatherDescription(1L, "Main", "Desc", "Icon")
    ),
    NetworkWeatherCondition(22.3, 22.2, 22.2, 67.9, 903.4)
)

fun createNewWeatherForecast(date: String): WeatherForecast {
    return WeatherForecast(
        1, date, Wind(22.2, 21),
        listOf(
            NetworkWeatherDescription(1L, "Main", "Desc", "Icon")
        ),
        NetworkWeatherCondition(22.3, 22.2, 22.2, 34.5, 67.8)
    )
}

val fakeWeatherForecastList = listOf(
    createNewWeatherForecast("23 Jan 2024, 2:00pm"),
    createNewWeatherForecast("24 Jan 2024, 12:00am"),
    createNewWeatherForecast("25 Jan 2024, 12:00am"),
    createNewWeatherForecast("26 Jan 2024, 12:00am"),
    createNewWeatherForecast("27 Jan 2024, 12:00am")
)

val invalidDataException = Exception("Invalid Data")
const val queryLocation = "Kampala"
const val cityId = 1234
