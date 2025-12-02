package com.app.noteapp.presentation.screens.add_note

import androidx.annotation.StringRes
import com.app.noteapp.presentation.model.ToastType

sealed interface NoteDetailEvents {
    data object NavigateToHomeScreen : NoteDetailEvents
    data class ShowToast(
        @StringRes val messageRes: Int,
        val type: ToastType
    ) : NoteDetailEvents
    data object OpenReminderPicker : NoteDetailEvents
    data object RequestDeleteConfirm : NoteDetailEvents
}