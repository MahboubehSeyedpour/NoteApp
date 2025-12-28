package com.app.noteapp.domain.common_model

import androidx.compose.runtime.Immutable
import com.app.noteapp.data.local.model.enums.MediaKind

@Immutable
sealed class NoteBlock {
    abstract val id: Long
    abstract val noteId: Long
    abstract val position: Int
    abstract val createdAt: Long
    abstract val updatedAt: Long
    abstract val deletedAt: Long?

    @Immutable
    data class Text(
        override val id: Long,
        override val noteId: Long,
        override val position: Int,
        override val createdAt: Long,
        override val updatedAt: Long,
        override val deletedAt: Long?,
        val text: String,
    ) : NoteBlock()

    @Immutable
    data class Media(
        override val id: Long,
        override val noteId: Long,
        override val position: Int,
        override val createdAt: Long,
        override val updatedAt: Long,
        override val deletedAt: Long?,
        val kind: MediaKind,
        val localUri: String,
        val mimeType: String?,
        val widthPx: Int?,
        val heightPx: Int?,
        val durationMs: Long?,
        val sizeBytes: Long?,
    ) : NoteBlock()
}