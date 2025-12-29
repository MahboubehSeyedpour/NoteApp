package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.entity.DirectoryEntity
import com.app.noteapp.domain.model.common_model.Directory

fun DirectoryEntity.toDomain(): Directory =
    Directory(
        id = id,
        userId = userId,
        parentId = parentId,
        name = name,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )