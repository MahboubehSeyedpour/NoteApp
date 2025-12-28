package com.app.noteapp.domain.common_model

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val id: Long,
    val email: String?,
    val phone: String?,
    val name: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)