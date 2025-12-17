package com.app.noteapp.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class NoteUiModel(
    val id: Long = 0,
    val title: String = "",
    val description: String? = "",
    val timeBadge: String? = null,
    val reminderAt: Long? = null,
    val createdAt: Long = 0L,
    val pinned: Boolean = false,
    val tag: TagUiModel? = null,
    val reminderTag: TagUiModel? = null
)