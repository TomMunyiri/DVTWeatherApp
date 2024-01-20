package com.tommunyiri.dvtweatherapp.ui

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tommunyiri.dvtweatherapp.utils.UserInteractionAwareCallback


/**
 * Created by Tom Munyiri on 20/01/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */
class BaseBottomSheetDialog(private val activity: Activity, theme: Int) :
    BottomSheetDialog(activity, theme) {

    override fun onStart() {
        super.onStart()
        this.window?.let {
            it.callback = UserInteractionAwareCallback(it.callback, activity)
        }
    }
}