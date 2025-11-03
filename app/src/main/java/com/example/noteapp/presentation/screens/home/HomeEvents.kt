package com.example.noteapp.presentation.screens.home

sealed class HomeEvents {
    data class NavigateToNoteDetailScreen(val noteId: Long?) : HomeEvents()
    data class Error(val message: String) : HomeEvents()
}
