package com.app.noteapp.presentation.model

import androidx.compose.runtime.Immutable
import com.app.noteapp.data.local.model.enums.BlockType
import com.app.noteapp.data.local.model.enums.MediaKind

@Immutable
data class NoteUiModel(
    val id: Long,
    val userId: Long,
    val directoryId: Long?,
    val title: String,
    val pinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
    val blocks: List<NoteBlockUiModel>,
    val tags: List<TagUiModel>,
    val reminders: List<ReminderUiModel>,
)

@Immutable
sealed class NoteBlockUiModel {
    abstract val id: Long
    abstract val noteId: Long
    abstract val position: Int
    abstract val createdAt: Long
    abstract val updatedAt: Long
    abstract val deletedAt: Long?
    abstract val type: BlockType

    @Immutable
    data class Text(
        override val id: Long,
        override val noteId: Long,
        override val position: Int,
        override val createdAt: Long,
        override val updatedAt: Long,
        override val deletedAt: Long?,
        override val type: BlockType = BlockType.TEXT,
        val text: String,
    ) : NoteBlockUiModel()

    @Immutable
    data class Media(
        override val id: Long,
        override val noteId: Long,
        override val position: Int,
        override val createdAt: Long,
        override val updatedAt: Long,
        override val deletedAt: Long?,
        override val type: BlockType = BlockType.MEDIA,
        val kind: MediaKind,
        val localUri: String,
        val mimeType: String?,
        val widthPx: Int?,
        val heightPx: Int?,
        val durationMs: Long?,
        val sizeBytes: Long?,
    ) : NoteBlockUiModel()
}

