package com.app.noteapp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.app.noteapp.presentation.model.ToastUI
import kotlinx.coroutines.delay

@Composable
fun ToastHost(
    toast: ToastUI?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (toast == null) return

    LaunchedEffect(toast) {
        delay(3000)
        onDismiss()
    }

    Box(
        modifier = modifier
            .wrapContentHeight()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        CustomToast(
            message = stringResource(toast.message),
            type = toast.type,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
