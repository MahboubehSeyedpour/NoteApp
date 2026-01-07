package com.app.noteapp.presentation.screens.notedetail.contract

import com.app.noteapp.presentation.model.NoteUiModel

data class NoteDetailState(
    val isLoading: Boolean = false,
    val editMode: Boolean = false,
    val note: NoteUiModel? = null
)