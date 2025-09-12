package com.example.noteapp.presentation.screens.note_details

sealed class NoteDetailEvents {
    data object NavigateToHomeScreen : NoteDetailEvents()
    data class Error(val message: String) : NoteDetailEvents()
}