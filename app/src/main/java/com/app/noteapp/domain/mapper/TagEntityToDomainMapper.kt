package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.domain.common_model.Tag

fun TagEntity.toDomain(): Tag =
    Tag(
        id = id,
        userId = userId,
        name = name,
        colorArgb = colorArgb,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )