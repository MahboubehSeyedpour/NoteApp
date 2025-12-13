package com.app.noteapp.domain.backup_model

import kotlinx.serialization.Serializable

@Serializable
data class TagBackupDto(
    val id: Long,
    val name: String,
    val colorArgb: Long,
)