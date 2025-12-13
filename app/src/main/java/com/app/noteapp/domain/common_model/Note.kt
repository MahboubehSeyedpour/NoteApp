package com.app.noteapp.domain.common_model

import androidx.compose.runtime.Immutable

@Immutable
data class Note(
    val id: Long = 0,
    val title: String = "",
    val description: String? = "",
    val timeBadge: String? = null,
    val reminderAt: Long? = null,
    val createdAt: Long = 0L,
    val pinned: Boolean = false,
    val tag: Tag? = null,
    val reminderTag: Tag? = null
)