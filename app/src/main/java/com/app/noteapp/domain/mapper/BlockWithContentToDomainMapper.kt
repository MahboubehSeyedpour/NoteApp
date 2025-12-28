package com.app.noteapp.domain.mapper

import com.app.noteapp.data.local.model.enums.BlockType
import com.app.noteapp.data.local.model.enums.MediaKind
import com.app.noteapp.data.local.relation.BlockWithContent
import com.app.noteapp.domain.common_model.NoteBlock

fun BlockWithContent.toDomain(): NoteBlock {
    val b = block

    return when (b.type) {
        BlockType.TEXT ->
            NoteBlock.Text(
                id = b.id,
                noteId = b.noteId,
                position = b.position,
                createdAt = b.createdAt,
                updatedAt = b.updatedAt,
                deletedAt = b.deletedAt,
                text = text?.text ?: ""
            )

        BlockType.MEDIA ->
            NoteBlock.Media(
                id = b.id,
                noteId = b.noteId,
                position = b.position,
                createdAt = b.createdAt,
                updatedAt = b.updatedAt,
                deletedAt = b.deletedAt,
                kind = media?.kind ?: MediaKind.IMAGE,
                localUri = media?.localUri.orEmpty(),
                mimeType = media?.mimeType,
                widthPx = media?.widthPx,
                heightPx = media?.heightPx,
                durationMs = media?.durationMs,
                sizeBytes = media?.sizeBytes,
            )
    }
}

fun BlockWithContent.toDomainBlockOrNull(): NoteBlock? {
    return when (block.type) {
        BlockType.TEXT -> {
            val textEntity = text ?: return null
            NoteBlock.Text(
                id = block.id,
                noteId = block.noteId,
                position = block.position,
                createdAt = block.createdAt,
                updatedAt = block.updatedAt,
                deletedAt = block.deletedAt,
                text = textEntity.text,
            )
        }

        BlockType.MEDIA -> {
            val mediaEntity = media ?: return null
            NoteBlock.Media(
                id = block.id,
                noteId = block.noteId,
                position = block.position,
                createdAt = block.createdAt,
                updatedAt = block.updatedAt,
                deletedAt = block.deletedAt,
                kind = mediaEntity.kind,
                localUri = mediaEntity.localUri,
                mimeType = mediaEntity.mimeType,
                widthPx = mediaEntity.widthPx,
                heightPx = mediaEntity.heightPx,
                durationMs = mediaEntity.durationMs,
                sizeBytes = mediaEntity.sizeBytes,
            )
        }
    }
}