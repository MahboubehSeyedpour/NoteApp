package com.example.noteapp.core.permissions

sealed interface PermissionRequest {
    data class Runtime(val permission: String) : PermissionRequest
    sealed interface Special : PermissionRequest {
        data object ScheduleExactAlarms : Special
    }
}

enum class PermissionResult {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}
