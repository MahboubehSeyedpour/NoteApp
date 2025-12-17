package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.domain.common_model.Tag

fun TagEntity.toTag(): Tag =
    Tag(id = id, name = name, color = colorArgb)