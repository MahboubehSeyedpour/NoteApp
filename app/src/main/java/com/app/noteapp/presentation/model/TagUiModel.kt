package com.app.noteapp.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TagUiModel(
    val id: Long,
    val name: String,
    val color: Color,
)
