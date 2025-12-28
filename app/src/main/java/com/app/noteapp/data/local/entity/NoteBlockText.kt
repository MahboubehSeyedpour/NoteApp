package com.app.noteapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_block_text",
    foreignKeys = [
        ForeignKey(
            entity = NoteBlockEntity::class,
            parentColumns = ["id"],
            childColumns = ["block_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("block_id")]
)
data class NoteBlockTextEntity(
    @PrimaryKey
    @ColumnInfo(name = "block_id") val blockId: Long,

    @ColumnInfo(name = "text") val text: String
)