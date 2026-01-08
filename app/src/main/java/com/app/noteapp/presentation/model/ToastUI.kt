package com.app.noteapp.presentation.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class ToastUI(
    @StringRes val message: Int,
    val type: ToastType
)