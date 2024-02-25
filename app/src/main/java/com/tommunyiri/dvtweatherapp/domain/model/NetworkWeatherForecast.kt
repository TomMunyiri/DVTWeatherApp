package com.tommunyiri.dvtweatherapp.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

data class NetworkWeatherForecast(

    val id: Int,

    @SerializedName("dt_txt")
    val date: String,

    val wind: Wind,

    @SerializedName("weather")
    val networkWeatherDescription: List<NetworkWeatherDescription>,

    @SerializedName("main")
    val networkWeatherCondition: NetworkWeatherCondition
)
