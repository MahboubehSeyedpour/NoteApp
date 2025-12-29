package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteBlockMediaEntity
import com.app.noteapp.data.local.entity.NoteBlockTextEntity
import com.app.noteapp.data.local.model.enums.BlockType
import com.app.noteapp.data.utils.normalizeTs
import com.app.noteapp.domain.model.common_model.NoteBlock

fun NoteBlock.toBlockEntity(
    serverId: String? = null,
    version: Long = 0,
    dirty: Boolean = true,
    now: Long = System.currentTimeMillis()
): NoteBlockEntity {
    require(position >= 0) { "NoteBlock.position must be >= 0" }

    val type = when (this) {
        is NoteBlock.Text -> BlockType.TEXT
        is NoteBlock.Media -> BlockType.MEDIA
    }

    return NoteBlockEntity(
        id = id,
        noteId = noteId,
        position = position,
        type = type,
        createdAt = normalizeTs(createdAt, now),
        updatedAt = normalizeTs(updatedAt, now),
        deletedAt = deletedAt,
        serverId = serverId,
        version = version,
        dirty = dirty
    )
}

fun NoteBlock.Text.toTextEntity(blockId: Long = this.id): NoteBlockTextEntity {
    require(text.isNotBlank()) { "Text block content must not be blank" }

    return NoteBlockTextEntity(
        blockId = blockId,
        text = text
    )
}

fun NoteBlock.Media.toMediaEntity(blockId: Long = this.id): NoteBlockMediaEntity {
    require(localUri.isNotBlank()) { "Media.localUri must not be blank" }

    return NoteBlockMediaEntity(
        blockId = blockId,
        kind = kind,
        localUri = localUri,
        mimeType = mimeType?.takeIf { it.isNotBlank() },
        widthPx = widthPx,
        heightPx = heightPx,
        durationMs = durationMs,
        sizeBytes = sizeBytes
    )
}


