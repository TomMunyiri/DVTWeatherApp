package com.tommunyiri.dvtweatherapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tommunyiri.dvtweatherapp.R

/**
 * Created by Tom Munyiri on 24/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleInputDialog(
    defaultValue: String,
    onSubmitButtonClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var cacheDuration by rememberSaveable { mutableStateOf(defaultValue) }
    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(10.dp)
        ) {

            Column(modifier = Modifier.padding(15.dp)) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(id = R.string.cache_string)
                )
                Spacer(modifier = Modifier.width(15.dp))
                TextField(
                    value = cacheDuration,
                    onValueChange = { cacheDuration = it },
                    //label = { Text(text = stringResource(id = R.string.enter_cache_duration)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Row {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            modifier = Modifier
                                .clickable(onClick = {
                                    onDismissRequest.invoke()
                                })
                                .padding(10.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.ok),
                            modifier = Modifier
                                .clickable(onClick = {
                                    onSubmitButtonClick.invoke(cacheDuration)
                                    onDismissRequest.invoke()
                                })
                                .padding(10.dp)
                        )
                    }
                }
            }

        }
    }
}