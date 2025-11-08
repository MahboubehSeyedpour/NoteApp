package com.app.noteapp.data.mapper

import androidx.compose.ui.graphics.Color
import com.app.noteapp.core.time.formatReminderEpoch
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.domain.model.Note
import com.app.noteapp.domain.model.Tag
import com.app.noteapp.presentation.theme.ReminderTagColor
import java.time.ZoneId


fun NoteEntity.toUI(tag: Tag?, zoneId: ZoneId = ZoneId.systemDefault()): Note = Note(
    id = id,
    title = title,
    description = description,
    tag = tag,
    reminderAt = reminderAt,
    pinned = pinned,
    createdAt = createdAt,
    timeBadge = reminderAt?.let { formatReminderEpoch(it, zoneId) },
    reminderTag = reminderAt?.let {
        Tag(
            id = -100L, name = formatReminderEpoch(reminderAt, zoneId), color = Color(ReminderTagColor.value)
        )
    })

fun Note.toDomain(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    description = description,
    tagId = tag?.id,
    reminderAt = reminderAt,
    pinned = pinned,
    createdAt = createdAt,
)
