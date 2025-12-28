package com.app.noteapp.domain.common_model

import androidx.compose.runtime.Immutable

@Immutable
data class Directory(
    val id: Long,
    val userId: Long,
    val parentId: Long?,
    val name: String,
    val sortOrder: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)