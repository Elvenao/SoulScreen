package com.example.mongodb.core

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mongodb.utils.fixUtcToLocalMillis
import com.example.mongodb.utils.getTodaySystemDateInMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis ?: getTodaySystemDateInMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel",
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(fixUtcToLocalMillis(datePickerState.selectedDateMillis))
                    onDismiss()
                }
            ) {
                Text("Done",
                    color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}