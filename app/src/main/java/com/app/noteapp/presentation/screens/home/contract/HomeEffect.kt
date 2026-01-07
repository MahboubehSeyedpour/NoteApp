package com.app.noteapp.presentation.screens.home.contract

import androidx.annotation.StringRes
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.ToastType

sealed interface HomeEffect {

    data class Toast(
        @StringRes val message: Int,
        val type: ToastType
    ) : HomeEffect

    data class Dialog(
        val spec: AppDialogSpec
    ) : HomeEffect

    data class NavigateToNoteDetail(val noteId: Long?) : HomeEffect
    data object NavigateToSettings : HomeEffect
}
