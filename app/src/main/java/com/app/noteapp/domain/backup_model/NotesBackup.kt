package com.app.noteapp.domain.backup_model

import kotlinx.serialization.Serializable

@Serializable
data class NotesBackupDto(
    val schemaVersion: Int,
    val exportedAtEpochMs: Long,
    val appVersion: String,
    val tags: List<TagBackupDto>,
    val notes: List<NoteBackupDto>,
)