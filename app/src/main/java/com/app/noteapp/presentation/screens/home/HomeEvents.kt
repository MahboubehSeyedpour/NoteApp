package com.app.noteapp.presentation.screens.home

import androidx.annotation.StringRes
import com.app.noteapp.presentation.model.ToastType

sealed interface HomeEvents {
    data class NavigateToNoteDetailScreen(val noteId: Long?) : HomeEvents
    data object NavigateToSettingsScreen : HomeEvents
    data class Error(val message: String) : HomeEvents
    data object RequestDeleteConfirm : HomeEvents
    data class ShowToast(
        @StringRes val messageRes: Int,
        val type: ToastType
    ) : HomeEvents
}
