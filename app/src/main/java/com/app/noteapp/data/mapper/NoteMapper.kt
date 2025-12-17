package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.domain.backup_model.NoteBackupDto

fun NoteBackupDto.toNoteEntity(): NoteEntity = NoteEntity(
    id = id,
    title = title,
    description = description,
    tagId = tagId,
    reminderAt = reminderAtEpochMs,
    pinned = pinned,
    createdAt = System.currentTimeMillis(),
)
