package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.UserEntity
import com.app.noteapp.data.utils.normalizeTs
import com.app.noteapp.domain.common_model.User

fun User.toEntity(
    serverId: String? = null,
    version: Long = 0,
    dirty: Boolean = true,
    now: Long = System.currentTimeMillis()
): UserEntity {
    require(email?.isBlank() != true) { "email must be null or non-blank" }
    require(phone?.isBlank() != true) { "phone must be null or non-blank" }
    require(name?.isBlank() != true) { "name must be null or non-blank" }

    return UserEntity(
        id = id,
        email = email?.takeIf { it.isNotBlank() },
        phone = phone?.takeIf { it.isNotBlank() },
        name = name?.takeIf { it.isNotBlank() },
        createdAt = normalizeTs(createdAt, now),
        updatedAt = normalizeTs(updatedAt, now),
        deletedAt = deletedAt,
        serverId = serverId,
        version = version,
        dirty = dirty
    )
}