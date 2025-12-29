package com.app.noteapp.domain.model.backup_model

import kotlinx.serialization.Serializable

@Serializable
data class NoteBackupDto(
    val id: Long,
    val title: String,
    val description: String? = null,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long? = null,
    val pinned: Boolean,
    val tagId: Long? = null,
    val reminderAtEpochMs: Long? = null,
)