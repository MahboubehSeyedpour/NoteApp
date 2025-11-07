package com.app.noteapp.core.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import kotlinx.coroutines.launch

@Composable
fun rememberPermissionRequester(): (PermissionRequest, (PermissionResult) -> Unit) -> Unit {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val scope = rememberCoroutineScope()

    // --- Runtime permission state ---
    var pendingPermission by remember { mutableStateOf<String?>(null) }
    var singleRuntimeContinuation by remember { mutableStateOf<((PermissionResult) -> Unit)?>(null) }

    val singleRuntimeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val perm = pendingPermission
        val cont = singleRuntimeContinuation
        if (perm != null && cont != null) {
            val permanentlyDenied = if (!granted) {
                activity?.let { !ActivityCompat.shouldShowRequestPermissionRationale(it, perm) } ?: false
            } else false
            cont(
                when {
                    granted -> PermissionResult.GRANTED
                    permanentlyDenied -> PermissionResult.PERMANENTLY_DENIED
                    else -> PermissionResult.DENIED
                }
            )
        }
        pendingPermission = null
        singleRuntimeContinuation = null
    }

    // --- Special access (Schedule Exact Alarms) ---
    var specialContinuation by remember { mutableStateOf<((PermissionResult) -> Unit)?>(null) }
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // After returning from Settings, re-check capability
        scope.launch {
            withFrameNanos { /* wait a frame */ }
            val ok = PermissionCheckers.canScheduleExactAlarms(context)
            specialContinuation?.invoke(if (ok) PermissionResult.GRANTED else PermissionResult.DENIED)
            specialContinuation = null
        }
    }

    return remember {
        { request: PermissionRequest, onResult: (PermissionResult) -> Unit ->
            when (request) {
                is PermissionRequest.Runtime -> {
                    if (PermissionCheckers.isRuntimePermissionGranted(context, request.permission)) {
                        onResult(PermissionResult.GRANTED)
                    } else {
                        pendingPermission = request.permission
                        singleRuntimeContinuation = onResult
                        singleRuntimeLauncher.launch(request.permission)
                    }
                }
                PermissionRequest.Special.ScheduleExactAlarms -> {
                    if (PermissionCheckers.canScheduleExactAlarms(context)) {
                        onResult(PermissionResult.GRANTED)
                    } else {
                        specialContinuation = onResult
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        settingsLauncher.launch(intent)
                    }
                }
            }
        }
    }
}
