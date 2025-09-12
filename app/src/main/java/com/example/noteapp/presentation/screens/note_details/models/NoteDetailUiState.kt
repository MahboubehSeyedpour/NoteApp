package com.example.noteapp.presentation.screens.note_details.models

import com.example.noteapp.presentation.model.NoteUI

data class NoteDetailUiState(
    val isLoading: Boolean = true,
    val note: NoteUI? = null,
    val isSaving: Boolean = false
)