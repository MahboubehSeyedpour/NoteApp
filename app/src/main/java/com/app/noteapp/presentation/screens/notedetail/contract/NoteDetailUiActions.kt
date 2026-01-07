package com.app.noteapp.presentation.screens.notedetail.contract

data class NoteDetailUiActions(
    val onConfirmDelete: () -> Unit,
    val onDismissDialog: () -> Unit,
    val onOpenSettings: () -> Unit
)