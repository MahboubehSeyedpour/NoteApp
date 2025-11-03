package com.example.noteapp.presentation.screens.add_note

sealed interface NoteDetailEvents {
    data object NavigateToHomeScreen : NoteDetailEvents
    data class Error(val message: String) : NoteDetailEvents
    data object OpenReminderPicker : NoteDetailEvents
}