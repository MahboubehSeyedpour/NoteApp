package com.app.noteapp.presentation.mapper

import androidx.compose.ui.graphics.Color
import com.app.noteapp.domain.model.common_model.Tag
import com.app.noteapp.presentation.model.TagUiModel

fun Tag.toUi(): TagUiModel =
    TagUiModel(
        id = id,
        name = name,
        color = Color(colorArgb)
    )