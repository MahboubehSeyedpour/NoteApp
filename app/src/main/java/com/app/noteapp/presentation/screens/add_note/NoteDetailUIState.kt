package com.app.noteapp.presentation.screens.add_note

import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.domain.common_model.Tag

data class NoteDetailUIState(
    val isLoading: Boolean = false,
    val note: Note? = null,
    val selectedTag: Tag? = null,
    val reminderAtMillis: Long? = null
)