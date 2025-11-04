package com.example.noteapp.data.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.noteapp.data.local.entity.TagEntity
import com.example.noteapp.domain.model.Tag

fun TagEntity.toUI(): Tag =
    Tag(id = id, name = name, color = Color(colorArgb))

fun Tag.toDomain(): TagEntity =
    TagEntity(id = id, name = name, colorArgb = color.toArgb())
