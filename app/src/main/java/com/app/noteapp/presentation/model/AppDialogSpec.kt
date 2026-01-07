package com.app.noteapp.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class AppDialogSpec(
    val type: DialogType,
    @StringRes val messageRes: Int,
    @StringRes val confirmTextRes: Int,
    @StringRes val dismissTextRes: Int,
    val showTopBar: Boolean = true,
    @DrawableRes val imageRes: Int? = null,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit,
)

enum class DialogType {
    PERMISSION,
    WARNING,
    ERROR
}
