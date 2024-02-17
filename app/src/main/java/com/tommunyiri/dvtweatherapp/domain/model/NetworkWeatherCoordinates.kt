package com.tommunyiri.dvtweatherapp.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkWeatherCoordinates(
    val lat: Double,
    val lon: Double
) : Parcelable