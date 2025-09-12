package com.example.noteapp.presentation.screens.home.model

import androidx.compose.runtime.Immutable

@Immutable
data class NoteUi(
    val id: String,
    val title: String,
    val body: String,
    val timeBadge: String?,
    val categoryBadge: String?
)