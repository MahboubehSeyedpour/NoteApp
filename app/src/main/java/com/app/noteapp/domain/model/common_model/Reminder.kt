package com.app.noteapp.domain.model.common_model

import androidx.compose.runtime.Immutable

@Immutable
data class Reminder(
    val id: Long,
    val noteId: Long,
    val triggerAt: Long,
    val isEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)