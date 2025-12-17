package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.domain.common_model.Tag

fun Tag.toTagEntity() = TagEntity(
    id = 0L,
    name = name,
    colorArgb = color
)