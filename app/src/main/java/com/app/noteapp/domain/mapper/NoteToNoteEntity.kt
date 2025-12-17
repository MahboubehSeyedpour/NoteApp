package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.domain.common_model.Note

fun Note.toNoteEntity() = NoteEntity(
    id = id,
    title = title,
    tagId = tagId,
    description = description,
    reminderAt = reminderAt,
    pinned = pinned,
    createdAt = createdAt
)