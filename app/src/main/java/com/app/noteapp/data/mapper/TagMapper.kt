package com.app.noteapp.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.domain.common_model.Tag

fun TagEntity.toUI(): Tag =
    Tag(id = id, name = name, color = Color(colorArgb))

fun Tag.toDomain(): TagEntity =
    TagEntity(id = id, name = name, colorArgb = color.toArgb())
