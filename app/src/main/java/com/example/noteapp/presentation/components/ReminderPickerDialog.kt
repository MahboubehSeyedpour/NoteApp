package com.example.noteapp.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPickerDialog(
    initialDateMillis: Long = System.currentTimeMillis(),
    initialHour: Int = 8,
    initialMinute: Int = 30,
    onDismiss: () -> Unit,
    onConfirm: (dateMillis: Long, hour: Int, minute: Int) -> Unit
) {
    var stage by remember { mutableIntStateOf(0) } // 0 = date, 1 = time

    val dateState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val timeState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = false)

    if (stage == 0) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = { TextButton(onClick = { stage = 1 }) { Text("Next") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
        ) {
            DatePicker(state = dateState)
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    val date = dateState.selectedDateMillis ?: System.currentTimeMillis()
                    onConfirm(date, timeState.hour, timeState.minute)
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { stage = 0 }) { Text("Back") } },
            text = { TimePicker(state = timeState) }
        )
    }
}
