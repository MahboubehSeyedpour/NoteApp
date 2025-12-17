package com.app.noteapp.presentation.mapper

import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.presentation.model.NoteUiModel

fun NoteUiModel.toNote() = Note(
    id = id,
    title = title,
    description = description,
    timeBadge = timeBadge,
    reminderAt = reminderAt,
    createdAt = createdAt,
    pinned = pinned,
    tagId = tag?.id,
    tag = tag?.toTag(),
    reminderTag = reminderTag?.toTag()
)