package com.tommunyiri.dvtweatherapp.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.talhafaki.composablesweettoast.util.SweetToastUtil
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetSuccess


/**
 * Created by Tom Munyiri on 27/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Composable
fun SweetToast(text: String, success: Boolean) {
    if (success) {
        SweetSuccess(
            message = text,
            duration = Toast.LENGTH_SHORT,
            padding = PaddingValues(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        )
    } else {
        SweetToastUtil.SweetError(
            message = text,
            duration = Toast.LENGTH_LONG,
            padding = PaddingValues(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        )
    }
}