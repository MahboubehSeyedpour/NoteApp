package com.app.noteapp.presentation.navigation

sealed class Screens(val route: String) {
    data object HomeScreen : Screens("home")
    data object NoteDetailScreen : Screens("details")
    data object Settings : Screens("settings")
}