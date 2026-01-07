package com.app.noteapp.presentation.screens.notedetail.contract

sealed interface NoteDetailEvent {
    data object NavigateHome : NoteDetailEvent
    data object SaveFailed : NoteDetailEvent
    data object DeleteFailed : NoteDetailEvent
    data object DeleteConfirmationRequired : NoteDetailEvent
    data object ImagePermissionDenied : NoteDetailEvent
    data object OpenReminderPicker : NoteDetailEvent
}