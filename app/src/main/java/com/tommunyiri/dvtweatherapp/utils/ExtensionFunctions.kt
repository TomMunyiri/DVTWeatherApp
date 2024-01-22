package com.tommunyiri.dvtweatherapp.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Created by Tom Munyiri on 19/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun String.formatDate(): String? {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    //val formatter = SimpleDateFormat("d MMM y, h:mma")
    //val formatter = SimpleDateFormat("EEE MMM d, HH:mma",Locale.ENGLISH)
    val formatter = SimpleDateFormat("EEE, HH:mma", Locale.ENGLISH)
    return parser.parse(this)?.let { formatter.format(it) }
}

fun Fragment.checkPlayServices(): Boolean {
    val gApi = GoogleApiAvailability.getInstance()
    val resultCode = gApi.isGooglePlayServicesAvailable(requireContext())
    if (resultCode != ConnectionResult.SUCCESS) {
        if (gApi.isUserResolvableError(resultCode)) {
            gApi.getErrorDialog(
                this, resultCode,
                GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE
            )?.show()
        } else {
            // No google play services
        }
        return false
    }
    return true
}