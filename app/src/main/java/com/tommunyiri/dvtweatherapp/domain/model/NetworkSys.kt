package com.tommunyiri.dvtweatherapp.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetworkSys(
    val country: String,
    val sunrise: Int,
    val sunset: Int
) : Parcelable