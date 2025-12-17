package com.app.noteapp.presentation.mapper

import androidx.compose.ui.graphics.toArgb
import com.app.noteapp.domain.common_model.Tag
import com.app.noteapp.presentation.model.TagUiModel

fun TagUiModel.toTag() = Tag(
    id = id,
    name = name,
    color = color.toArgb()
)