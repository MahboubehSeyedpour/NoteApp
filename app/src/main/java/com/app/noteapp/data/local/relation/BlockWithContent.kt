package com.app.noteapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteBlockMediaEntity
import com.app.noteapp.data.local.entity.NoteBlockTextEntity

data class BlockWithContent(
    @Embedded val block: NoteBlockEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "block_id",
        entity = NoteBlockTextEntity::class
    )
    val text: NoteBlockTextEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "block_id",
        entity = NoteBlockMediaEntity::class
    )
    val media: NoteBlockMediaEntity?
)