package com.app.noteapp.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmBtnClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmBtnText: @Composable RowScope.() -> Unit,
    dismissBtnText: @Composable RowScope.() -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        confirmButton = { TextButton(onClick = onConfirmBtnClick, content = confirmBtnText)},
        dismissButton = { TextButton(onClick = onDismissButtonClick, content = dismissBtnText)})
}