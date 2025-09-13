package com.example.noteapp.presentation.screens.home

sealed class HomeEvents {
    data class NavigateToNoteDetailsScreen(val noteId: Long) : HomeEvents()
    data object NavigateToAddNoteScreen : HomeEvents()
    data class Error(val message: String) : HomeEvents()
}
