package com.example.noteapp.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class NoteUI(
    val id: Long,
    val title: String,
    val description: String?,
    val categoryBadge: String?,
    val timeBadge: String?,
    val createdAt: Long
)