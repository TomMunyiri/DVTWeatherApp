package com.tommunyiri.dvtweatherapp.domain.utils

import java.text.DecimalFormat


/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

/**
 * This function converts a [number] from Kelvin to Celsius by using [DecimalFormat] and
 * converting it to a [Double] then subtracting 273 from it.
 * @param number the number to be converted to Celsius.
 */
fun convertKelvinToCelsius(number: Number): Double {
    return DecimalFormat().run {
        applyPattern(".##")
        parse(format(number.toDouble().minus(273)))?.toDouble() ?: 0.00
    }
}