package com.app.noteapp.presentation.mapper

import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.presentation.model.NoteUiModel

fun Note.toNoteUiModel(): NoteUiModel = NoteUiModel(
    id = id,
    title = title,
    description = description,
    tag = tag?.toTagUiMapper(),
    reminderAt = reminderAt,
    pinned = pinned,
    createdAt = createdAt,
    timeBadge = timeBadge,
    reminderTag = reminderTag?.toTagUiMapper()
)