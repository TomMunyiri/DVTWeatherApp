package com.tommunyiri.dvtweatherapp.domain.utils

import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */


fun String.formatDate(): String? {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    //val formatter = SimpleDateFormat("d MMM y, h:mma")
    //val formatter = SimpleDateFormat("EEE MMM d, HH:mma",Locale.ENGLISH)
    val formatter = SimpleDateFormat("EEE, HH:mma", Locale.ENGLISH)
    return parser.parse(this)?.let { formatter.format(it) }
}
