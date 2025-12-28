package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.relation.NoteWithRelations
import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.domain.common_model.Reminder
import com.app.noteapp.domain.common_model.Tag

fun NoteWithRelations.toDomain(): Note {
    return Note(
        id = note.id,
        userId = note.userId,
        directoryId = note.directoryId,
        title = note.title,
        pinned = note.pinned,
        createdAt = note.createdAt,
        updatedAt = note.updatedAt,
        deletedAt = note.deletedAt,
        blocks = blocks.sortedBy { it.block.position }.mapNotNull { it.toDomainBlockOrNull() },
        tags = tags.filter { it.deletedAt == null }.map { tag ->
                Tag(
                    id = tag.id,
                    userId = tag.userId,
                    name = tag.name,
                    colorArgb = tag.colorArgb,
                    createdAt = tag.createdAt,
                    updatedAt = tag.updatedAt,
                    deletedAt = tag.deletedAt,
                )
            },
        reminders = reminders.filter { it.deletedAt == null }.map { r ->
                Reminder(
                    id = r.id,
                    noteId = r.noteId,
                    triggerAt = r.triggerAt,
                    isEnabled = r.isEnabled,
                    createdAt = r.createdAt,
                    updatedAt = r.updatedAt,
                    deletedAt = r.deletedAt,
                )
            },
    )
}