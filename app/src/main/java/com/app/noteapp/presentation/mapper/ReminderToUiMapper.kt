package com.app.noteapp.presentation.mapper

import com.app.noteapp.domain.model.common_model.Reminder
import com.app.noteapp.presentation.model.ReminderUiModel

fun Reminder.toUi(): ReminderUiModel =
    ReminderUiModel(
        id = id,
        noteId = noteId,
        triggerAt = triggerAt,
        isEnabled = isEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )