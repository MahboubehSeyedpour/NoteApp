package com.app.noteapp.domain.model.common_model

import androidx.compose.runtime.Immutable

@Immutable
data class Note(
    val id: Long,
    val userId: Long,
    val directoryId: Long?,
    val title: String,
    val pinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,

    // aggregate relations:
    val blocks: List<NoteBlock>,
    val tags: List<Tag>,
    val reminders: List<Reminder>,
)
