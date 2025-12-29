package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.DirectoryEntity
import com.app.noteapp.data.utils.normalizeTs
import com.app.noteapp.domain.model.common_model.Directory

fun Directory.toEntity(
    serverId: String? = null,
    version: Long = 0,
    dirty: Boolean = true,
    now: Long = System.currentTimeMillis()
): DirectoryEntity {
    require(name.isNotBlank()) { "Directory.name must not be blank" }
    require(sortOrder >= 0) { "Directory.sortOrder must be >= 0" }

    return DirectoryEntity(
        id = id,
        userId = userId,
        parentId = parentId,
        name = name.trim(),
        sortOrder = sortOrder,
        createdAt = normalizeTs(createdAt, now),
        updatedAt = normalizeTs(updatedAt, now),
        deletedAt = deletedAt,
        serverId = serverId,
        version = version,
        dirty = dirty
    )
}