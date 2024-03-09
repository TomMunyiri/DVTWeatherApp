package com.tommunyiri.dvtweatherapp.domain.model

import kotlinx.serialization.Serializable

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Serializable
data class SearchResult(
    val name: String,
    val country: String,
    val subcountry: String,
)
