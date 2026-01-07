package com.app.noteapp.presentation.screens.home.contract

sealed interface HomeEvent {

    data class NavigateToNoteDetail(val noteId: Long?) : HomeEvent
    data object NavigateToSettings : HomeEvent

    data object DeleteConfirmationRequired : HomeEvent

    data object InvalidNoteId : HomeEvent
    data object DeleteFailed : HomeEvent
    data object PinFailed : HomeEvent
}
