package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.TagEntity
import com.app.noteapp.data.utils.normalizeTs
import com.app.noteapp.domain.model.common_model.Tag

fun Tag.toEntity(
    serverId: String? = null,
    version: Long = 0,
    dirty: Boolean = true,
    now: Long = System.currentTimeMillis()
): TagEntity {
    require(name.isNotBlank()) { "Tag.name must not be blank" }

    return TagEntity(
        id = id,
        userId = userId,
        name = name.trim(),
        colorArgb = colorArgb,
        createdAt = normalizeTs(createdAt, now),
        updatedAt = normalizeTs(updatedAt, now),
        deletedAt = deletedAt,
        serverId = serverId,
        version = version,
        dirty = dirty
    )
}