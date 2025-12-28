package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.entity.UserEntity
import com.app.noteapp.domain.common_model.User

fun UserEntity.toDomain(): User =
    User(
        id = id,
        email = email,
        phone = phone,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )