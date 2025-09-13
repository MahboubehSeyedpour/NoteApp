package com.example.noteapp.core.permissions

import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun rememberPermissionLaunchers(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): ManagedActivityResultLauncher<String, Boolean>? {
    return if (Build.VERSION.SDK_INT >= 33) {
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onGranted() else onDenied()
        }
    } else null
}