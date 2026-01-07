package com.app.noteapp.presentation.screens.notedetail.mapper

import com.app.noteapp.R
import com.app.noteapp.presentation.model.ToastType
import com.app.noteapp.presentation.screens.notedetail.contract.NoteDetailEffect
import com.app.noteapp.presentation.screens.notedetail.contract.NoteDetailEvent
import com.app.noteapp.presentation.screens.notedetail.contract.NoteDetailUiActions
import com.app.noteapp.presentation.screens.notedetail.ui.dialog.DialogPresets

class NoteDetailEffectMapper(
    private val actions: NoteDetailUiActions
) {

    fun map(event: NoteDetailEvent): NoteDetailEffect =
        when (event) {

            NoteDetailEvent.SaveFailed ->
                NoteDetailEffect.Toast(
                    message = R.string.fail_to_save_note,
                    type = ToastType.ERROR
                )

            NoteDetailEvent.DeleteFailed ->
                NoteDetailEffect.Toast(
                    message = R.string.fail_to_delete_note,
                    type = ToastType.ERROR
                )

            NoteDetailEvent.DeleteConfirmationRequired ->
                NoteDetailEffect.Dialog(
                    DialogPresets.deleteConfirm(actions)
                )

            NoteDetailEvent.ImagePermissionDenied ->
                NoteDetailEffect.Dialog(
                    DialogPresets.permissionRequired(actions)
                )

            NoteDetailEvent.NavigateHome ->
                NoteDetailEffect.NavigateBack

            NoteDetailEvent.OpenReminderPicker ->
                NoteDetailEffect.OpenReminderPicker
        }
}
