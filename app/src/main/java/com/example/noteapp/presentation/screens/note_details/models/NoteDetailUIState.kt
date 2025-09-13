package com.example.noteapp.presentation.screens.note_details.models

import com.example.noteapp.domain.model.Note

data class NoteDetailUIState(
    val isLoading: Boolean = true,
    val note: Note? = null,
    val isSaving: Boolean = false
)