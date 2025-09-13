package com.example.noteapp.presentation.mapper

import com.example.noteapp.core.time.formatReminderEpoch
import com.example.noteapp.data.local.note.NoteEntity
import com.example.noteapp.domain.model.Note
import java.time.ZoneId

fun NoteEntity.toUI(zoneId: ZoneId = ZoneId.systemDefault()): Note = Note(
    id = id,
    title = title,
    description = description,
    categoryBadge = category,
    timeBadge = reminderAt?.let { formatReminderEpoch(it, zoneId) },
    reminderAt = reminderAt,
    createdAt = createdAt,
    pinned = pinned
)

fun Note.toDomain(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    description = description,
    category = categoryBadge,
    reminderAt = reminderAt,   // already combined & stored
    pinned = false,
    createdAt = createdAt
)