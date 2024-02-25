package com.tommunyiri.dvtweatherapp.presentation.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


/**
 * Created by Tom Munyiri on 22/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Composable
fun ScreenTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(top = 50.dp, start = 15.dp, end = 20.dp)
    )
}