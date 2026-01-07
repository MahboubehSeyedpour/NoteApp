package com.app.noteapp.presentation.screens.home.mapper

import com.app.noteapp.R
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.model.ToastType
import com.app.noteapp.presentation.screens.home.contract.HomeEffect
import com.app.noteapp.presentation.screens.home.contract.HomeEvent
import com.app.noteapp.presentation.screens.home.contract.HomeUiActions

class HomeEffectMapper(
    private val actions: HomeUiActions
) {

    fun map(event: HomeEvent): HomeEffect =
        when (event) {

            is HomeEvent.NavigateToNoteDetail ->
                HomeEffect.NavigateToNoteDetail(event.noteId)

            HomeEvent.NavigateToSettings ->
                HomeEffect.NavigateToSettings

            HomeEvent.DeleteConfirmationRequired ->
                HomeEffect.Dialog(
                    AppDialogSpec(
                        type = DialogType.ERROR,
                        messageRes = R.string.delete_note_question,
                        confirmTextRes = R.string.delete,
                        dismissTextRes = R.string.dialog_dismiss_btn,
                        onConfirm = { /* wired in screen */ },
                        onDismiss = actions.onDismissDialog
                    )
                )

            HomeEvent.InvalidNoteId ->
                HomeEffect.Toast(
                    message = R.string.invalid_note_id,
                    type = ToastType.ERROR
                )

            HomeEvent.DeleteFailed ->
                HomeEffect.Toast(
                    message = R.string.fail_to_delete_note,
                    type = ToastType.ERROR
                )

            HomeEvent.PinFailed ->
                HomeEffect.Toast(
                    message = R.string.fail_to_pin_note,
                    type = ToastType.ERROR
                )
        }
}
