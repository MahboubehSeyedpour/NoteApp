package com.app.noteapp.presentation.common.permission

data class PermissionModel(
    val permission: String,
    val description: String,
    val required: Boolean
)