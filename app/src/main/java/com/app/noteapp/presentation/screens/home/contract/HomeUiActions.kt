package com.app.noteapp.presentation.screens.home.contract

data class HomeUiActions(
    val onConfirmDelete: (Long) -> Unit,
    val onDismissDialog: () -> Unit
)
