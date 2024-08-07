package com.tommunyiri.dvtweatherapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tom Munyiri on 21/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Parcelize
data class FavoriteLocation(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
) : Parcelable
