package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.entity.ReminderEntity
import com.app.noteapp.domain.model.common_model.Reminder

fun ReminderEntity.toDomain(): Reminder =
    Reminder(
        id = id,
        noteId = noteId,
        triggerAt = triggerAt,
        isEnabled = isEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )