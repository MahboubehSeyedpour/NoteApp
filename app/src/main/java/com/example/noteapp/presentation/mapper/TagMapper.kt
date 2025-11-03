package com.example.noteapp.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.noteapp.data.local.tag.TagEntity
import com.example.noteapp.domain.model.Tag

fun TagEntity.toUI(): Tag = Tag(name = name, color = Color(colorArgb))

fun Tag.toDomain(existingId: Long = 0L): TagEntity =
    TagEntity(id = existingId, name = name, colorArgb = color.toArgb())