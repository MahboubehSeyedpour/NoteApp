package com.app.noteapp.presentation.common.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun openAppSettingsInDevice(context: Context) {
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
    )
}