package com.app.noteapp.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmBtnClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    @StringRes confirmBtnText: Int,
    @StringRes dismissBtnText: Int,
) {
    AlertDialog(onDismissRequest = onDismissRequest, title = title, text = text, confirmButton = {
        TextButton(
            onClick = onConfirmBtnClick,
            content = { Text(text = stringResource(confirmBtnText)) })
    }, dismissButton = {
        TextButton(
            onClick = onDismissButtonClick,
            content = { Text(text = stringResource(dismissBtnText)) })
    })
}