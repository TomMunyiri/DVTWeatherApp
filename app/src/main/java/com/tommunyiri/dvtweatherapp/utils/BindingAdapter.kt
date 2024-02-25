package com.tommunyiri.dvtweatherapp.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.tommunyiri.dvtweatherapp.R
import com.tommunyiri.dvtweatherapp.data.sources.local.preferences.SharedPreferenceHelper


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
@BindingAdapter("setIcon")
fun ImageView.showIcon(condition: String?) {
    WeatherUtils.getIconResources(this, condition)
}

@BindingAdapter("setTemperature")
fun TextView.setTemperature(double: Double) {
    val context = this.context
    if (SharedPreferenceHelper.getInstance(context)
            .getSelectedTemperatureUnit() == context.getString(
            R.string.temp_unit_fahrenheit
        )
    )
        this.text = double.toString() + context.resources.getString(R.string.temp_symbol_fahrenheit)
    else
        this.text = double.toString() + context.resources.getString(R.string.temp_symbol_celsius)
}

@BindingAdapter("setMinTemperature")
fun TextView.setMinTemperature(double: Double) {
    val context = this.context
    if (SharedPreferenceHelper.getInstance(context)
            .getSelectedTemperatureUnit() == context.getString(
            R.string.temp_unit_fahrenheit
        )
    )
        this.text =
            "${double.toString() + context.resources.getString(R.string.temp_symbol_fahrenheit)}\nmin"
    else
        this.text =
            "${double.toString() + context.resources.getString(R.string.temp_symbol_celsius)}\nmin"
}

@BindingAdapter("setMaxTemperature")
fun TextView.setMaxTemperature(double: Double) {
    val context = this.context
    if (SharedPreferenceHelper.getInstance(context)
            .getSelectedTemperatureUnit() == context.getString(
            R.string.temp_unit_fahrenheit
        )
    )
        this.text =
            "${double.toString() + context.resources.getString(R.string.temp_symbol_fahrenheit)}\nmax"
    else
        this.text =
            "${double.toString() + context.resources.getString(R.string.temp_symbol_celsius)}\nmax"
}

@BindingAdapter("setCurrentTemperature")
fun TextView.setCurrentTemperature(double: Double) {
    val context = this.context
    if (SharedPreferenceHelper.getInstance(context)
            .getSelectedTemperatureUnit() == context.getString(
            R.string.temp_unit_fahrenheit
        )
    )
        this.text =
            "${double.toString() + context.resources.getString(R.string.temp_symbol_fahrenheit)}\ncurrent"
    else
        this.text =
            "${double.toString() + context.resources.getString(R.string.temp_symbol_celsius)}\ncurrent"
}