package com.example.noteapp.presentation.components

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.noteapp.R
import com.example.noteapp.core.extensions.canScheduleExactAlarms
import com.example.noteapp.core.extensions.hasPostNotificationsPermission
import com.example.noteapp.core.permissions.rememberPermissionLaunchers

@Composable
fun ReminderEntryPoint(
    openPicker: () -> Unit
) {
    val context = LocalContext.current
    val notifLauncher = rememberPermissionLaunchers(
        onGranted = { openPicker() },
        onDenied = {
            Toast.makeText(
                context,
                context.getString(R.string.permission_denied_notification),
                Toast.LENGTH_SHORT
            ).show()
        }
    )

    fun requestExactAlarmIfNeeded(): Boolean {
        if (Build.VERSION.SDK_INT >= 31 && !context.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
            }
            context.startActivity(intent)
            return false
        }
        return true
    }

    Button(onClick = {
        if (!requestExactAlarmIfNeeded()) return@Button

        if (!context.hasPostNotificationsPermission()) {
            notifLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            openPicker()
        }
    }) {
        Text(context.getString(R.string.reminder_set))
    }
}
