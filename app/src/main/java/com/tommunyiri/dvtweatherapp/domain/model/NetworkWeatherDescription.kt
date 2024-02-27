package com.tommunyiri.dvtweatherapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Parcelize
data class NetworkWeatherDescription(
    val id: Long,
    val main: String?,
    val description: String?,
    val icon: String?
) : Parcelable
