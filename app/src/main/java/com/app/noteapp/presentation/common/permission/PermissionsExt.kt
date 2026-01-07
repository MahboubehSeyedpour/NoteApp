package com.app.noteapp.presentation.common.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun List<PermissionModel>.areRequiredPermissionsGranted(
    context: Context
): Boolean {
    return this.asSequence().filter { it.required }.all {
        ContextCompat.checkSelfPermission(
            context, it.permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun List<PermissionModel>.missingRequiredPermissions(
    context: Context
): List<String> {
    return this.filter { it.required }.filter {
        ContextCompat.checkSelfPermission(
            context, it.permission
        ) != PackageManager.PERMISSION_GRANTED
    }.map { it.permission }
}

fun PermissionModel.isPermissionsGranted(
    context: Context
): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}