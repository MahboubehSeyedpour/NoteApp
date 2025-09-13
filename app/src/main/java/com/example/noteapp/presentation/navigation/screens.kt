package com.example.noteapp.presentation.navigation

sealed class Screens(val route: String) {
    data object HomeScreen : Screens("home")
    data object NoteDetailsScreen : Screens("details")
    data object AddNoteScreen : Screens("add")
}