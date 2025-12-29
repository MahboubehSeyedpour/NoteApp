package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.domain.model.common_model.Note

fun Note.toNoteEntity(): NoteEntity {
    require(userId > 0) { "userId must be > 0" }
    require(title.isNotBlank()) { "Note title must not be blank" }

    return NoteEntity(
        id = id,
        userId = userId,
        directoryId = directoryId,
        title = title,
        pinned = pinned,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        serverId = null,
        version = 0,
        dirty = true,
    )
}