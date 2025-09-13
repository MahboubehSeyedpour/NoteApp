package com.example.noteapp.core.enums

enum class PermissionType(val permission: String) {
    Notification(android.Manifest.permission.POST_NOTIFICATIONS),
    SCHEDULE_ALARM(android.Manifest.permission.SCHEDULE_EXACT_ALARM),
}