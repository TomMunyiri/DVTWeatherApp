package com.tommunyiri.dvtweatherapp.domain.usecases.preferences

import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper

/**
 * Created by Tom Munyiri on 25/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class GetSharedPreferencesUseCase(private val sharedPreferenceHelper: SharedPreferenceHelper) {
    operator fun invoke(): SharedPreferenceHelper {
        return sharedPreferenceHelper
    }
}
