package com.app.noteapp.presentation.screens.notedetail.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberPermissionRequester(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): (String) -> Unit {

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) onGranted() else onDenied()
        }

    return remember {
        { permission: String ->
            launcher.launch(permission)
        }
    }
}
