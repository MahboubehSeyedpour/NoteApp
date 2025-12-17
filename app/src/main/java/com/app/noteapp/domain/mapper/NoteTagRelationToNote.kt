package com.app.noteapp.domain.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.app.noteapp.core.time.formatReminderEpoch
import com.app.noteapp.data.local.relation.NoteTagRelation
import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.domain.common_model.Tag
import com.app.noteapp.presentation.theme.ReminderTagColor
import java.time.ZoneId

fun NoteTagRelation.toNote(zoneId: ZoneId = ZoneId.systemDefault()) = Note(
    id = note.id,
    title = note.title,
    description = note.description,
    tagId = tag?.id,
    tag = tag?.toTag(),
    reminderAt = note.reminderAt,
    pinned = note.pinned,
    createdAt = note.createdAt,
    timeBadge = note.reminderAt?.let { formatReminderEpoch(it, zoneId) },
    reminderTag = note.reminderAt?.let {
        Tag(
            id = -100L, name = formatReminderEpoch(note.reminderAt, zoneId), color = Color(ReminderTagColor.value).toArgb()
        )
    })