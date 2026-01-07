package com.app.noteapp.presentation.screens.notedetail.ui.dialog

import com.app.noteapp.R
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.screens.notedetail.contract.NoteDetailUiActions

object DialogPresets {

    fun deleteConfirm(actions: NoteDetailUiActions) = AppDialogSpec(
        type = DialogType.ERROR,
        messageRes = R.string.delete_note_question,
        confirmTextRes = R.string.delete,
        dismissTextRes = R.string.dialog_dismiss_btn,
        onConfirm = actions.onConfirmDelete,
        onDismiss = actions.onDismissDialog
    )

    fun permissionRequired(actions: NoteDetailUiActions) = AppDialogSpec(
        type = DialogType.PERMISSION,
        messageRes = R.string.permission_required_message,
        confirmTextRes = R.string.go_to_settings,
        dismissTextRes = R.string.no,
        onConfirm = actions.onOpenSettings,
        onDismiss = actions.onDismissDialog
    )
}
