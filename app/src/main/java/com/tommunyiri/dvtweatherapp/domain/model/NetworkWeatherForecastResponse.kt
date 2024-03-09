package com.tommunyiri.dvtweatherapp.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

data class NetworkWeatherForecastResponse(
    @SerializedName("list")
    val weathers: List<NetworkWeatherForecast>,
    val city: City,
)
