package com.example.noteapp.presentation.screens.home

sealed class HomeEvents {
    data class NavigateToMovieDetailsScreen(val interScreenData: String) : HomeEvents()
    data object NavigateToSearchScreen : HomeEvents()
}