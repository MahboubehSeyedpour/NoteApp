package com.app.noteapp.domain.common_model

import androidx.compose.runtime.Immutable

@Immutable
data class Tag(
    val id: Long,
    val userId: Long,
    val name: String,
    val colorArgb: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)
