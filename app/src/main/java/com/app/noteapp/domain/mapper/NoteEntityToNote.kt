package com.app.noteapp.domain.mapper


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.app.noteapp.core.time.formatReminderEpoch
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.domain.common_model.Tag
import com.app.noteapp.presentation.theme.ReminderTagColor
import java.time.ZoneId

fun NoteEntity.toNote(zoneId: ZoneId = ZoneId.systemDefault()): Note = Note(
    id = id,
    title = title,
    description = description,
    tagId = tagId,
    reminderAt = reminderAt,
    pinned = pinned,
    createdAt = createdAt,
    timeBadge = reminderAt?.let { formatReminderEpoch(it, zoneId) },
    reminderTag = reminderAt?.let {
        Tag(
            id = -100L,
            name = formatReminderEpoch(reminderAt, zoneId),
            color = Color(ReminderTagColor.value).toArgb()
        )
    })