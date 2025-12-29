package com.app.noteapp.presentation.screens.home

sealed class HomeEvents {
    data class NavigateToNoteDetailScreen(val noteId: Long?) : HomeEvents()
    data object NavigateToSettingsScreen : HomeEvents()
    data class Error(val message: String) : HomeEvents()
}
