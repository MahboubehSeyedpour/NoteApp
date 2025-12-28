package com.app.noteapp.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class ReminderUiModel(
    val id: Long,
    val noteId: Long,
    val triggerAt: Long,
    val isEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)