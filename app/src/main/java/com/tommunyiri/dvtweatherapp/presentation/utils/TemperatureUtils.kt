package com.tommunyiri.dvtweatherapp.presentation.utils

import java.text.DecimalFormat

/**
 * Created by Tom Munyiri on 18/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 *
 * This function converts [celsius] from Kelvin to Celsius by using [DecimalFormat] and
 * converting it to a [Double] then subtracting 273 from it.
 * @param celsius the number to be converted to Celsius.
 */

fun convertCelsiusToFahrenheit(celsius: Double): Double {
    return DecimalFormat().run {
        applyPattern(".##")
        parse(format(celsius.times(1.8).plus(32)))?.toDouble() ?: 0.00
    }
}
