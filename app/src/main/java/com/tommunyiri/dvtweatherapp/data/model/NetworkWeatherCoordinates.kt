package com.tommunyiri.dvtweatherapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkWeatherCoordinates(
    val lat: Double,
    val lon: Double
) : Parcelable