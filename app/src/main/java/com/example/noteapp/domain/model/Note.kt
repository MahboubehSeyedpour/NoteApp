package com.example.noteapp.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Note(
    val id: Long,
    val title: String,
    val description: String?,
    val categoryBadge: String?,
    val timeBadge: String?,
    val reminderAt: Long?,
    val createdAt: Long,
    val pinned: Boolean
)