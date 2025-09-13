package com.example.noteapp.core.extensions

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context.hasPostNotificationsPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= 33)
        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    else true

fun Context.canScheduleExactAlarms(): Boolean =
    if (Build.VERSION.SDK_INT >= 31) {
        val am = getSystemService(AlarmManager::class.java)
        am.canScheduleExactAlarms()
    } else true