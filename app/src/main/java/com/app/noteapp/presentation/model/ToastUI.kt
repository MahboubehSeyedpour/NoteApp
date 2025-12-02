package com.app.noteapp.presentation.model

import androidx.annotation.StringRes

data class ToastUI(
    @StringRes val message: Int,
    val type: ToastType
)