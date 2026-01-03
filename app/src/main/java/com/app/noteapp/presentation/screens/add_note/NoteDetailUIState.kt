package com.app.noteapp.presentation.screens.add_note

import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.TagUiModel

data class NoteDetailUIState(
    val isLoading: Boolean = false,
    val editMode: Boolean = false,
    val note: NoteUiModel? = null,
    val selectedTag: TagUiModel? = null,
    val reminderAtMillis: Long? = null
)