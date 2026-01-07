package com.app.noteapp.presentation.screens.notedetail.contract

import androidx.annotation.StringRes
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.ToastType

sealed interface NoteDetailEffect {

    data class Toast(
        @StringRes val message: Int,
        val type: ToastType
    ) : NoteDetailEffect

    data class Dialog(
        val spec: AppDialogSpec
    ) : NoteDetailEffect

    data object NavigateBack : NoteDetailEffect
    data object OpenReminderPicker : NoteDetailEffect
}