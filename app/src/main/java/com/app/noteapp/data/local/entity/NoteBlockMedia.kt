package com.app.noteapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.app.noteapp.data.local.model.enums.MediaKind

@Entity(
    tableName = "note_block_media",
    foreignKeys = [
        ForeignKey(
            entity = NoteBlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["block_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("block_id"),
        Index("kind"),
        Index("mime_type")
    ]
)
data class NoteBlockMediaEntity(
    @PrimaryKey
    @ColumnInfo(name = "block_id") val blockId: Long,

    @ColumnInfo(name = "kind") val kind: MediaKind,
    @ColumnInfo(name = "local_uri") val localUri: String,

    @ColumnInfo(name = "mime_type") val mimeType: String? = null,
    @ColumnInfo(name = "width_px") val widthPx: Int? = null,
    @ColumnInfo(name = "height_px") val heightPx: Int? = null,
    @ColumnInfo(name = "duration_ms") val durationMs: Long? = null,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long? = null
)
