package com.example.noteapp.presentation.screens.add_note

import com.example.noteapp.domain.model.Note
import com.example.noteapp.domain.model.Tag

data class NoteDetailUIState(
    val isLoading: Boolean = false,
    val note: Note? = null,
    val selectedTag: Tag? = null,
    val reminderAtMillis: Long? = null
)