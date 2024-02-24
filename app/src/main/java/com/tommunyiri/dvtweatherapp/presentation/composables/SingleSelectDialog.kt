package com.tommunyiri.dvtweatherapp.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tommunyiri.dvtweatherapp.R

/**
 * Created by Tom Munyiri on 23/02/2024.
 * Company: Eclectics International Ltd
 * Email: munyiri.thomas@eclectics.io
 */

@Composable
fun SingleSelectDialog(
    optionsList: List<String>,
    defaultSelected: Int,
    onCancelButtonClick: (Int) -> Unit,
    onItemSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {

    val selectedOption = mutableStateOf(defaultSelected)

    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(10.dp)
        ) {

            Column(modifier = Modifier.padding(10.dp)) {

                Spacer(modifier = Modifier.width(10.dp))

                LazyColumn(modifier = Modifier.width(500.dp)) {
                    items(optionsList.size) { i ->
                        SingleItemDialogRadioButton(
                            optionsList[i],
                            optionsList[selectedOption.value]
                        ) { selectedValue ->
                            onItemSelected.invoke(selectedValue)
                            selectedOption.value = optionsList.indexOf(selectedValue)
                            onDismissRequest.invoke()
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        modifier = Modifier
                            .clickable(onClick = {
                                onCancelButtonClick.invoke(selectedOption.value)
                                onDismissRequest.invoke()
                            })
                            .padding(10.dp)
                    )
                }
            }

        }
    }
}