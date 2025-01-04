package com.hfut.schedule.ui.utils.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LittleDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    conformtext : String,
    dismisstext : String
) {
        AlertDialog(
            // icon = { Icon(icon, contentDescription = "Example Icon") },
            title = { Text(text = dialogTitle) },
            text = { Text(text = dialogText) },
            onDismissRequest = { onDismissRequest() },
            confirmButton = {
                Button(onClick = { onConfirmation() }
                ) { Text(conformtext) }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { onDismissRequest() }
                ) { Text(dismisstext) }
            },
        )
}
