package com.app.noteapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.app.noteapp.data.local.entity.NoteBlockEntity
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.entity.NoteTagEntity
import com.app.noteapp.data.local.entity.ReminderEntity
import com.app.noteapp.data.local.entity.TagEntity

data class NoteWithRelations(
    @Embedded val note: NoteEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "note_id",
        entity = NoteBlockEntity::class
    )
    val blocks: List<BlockWithContent>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NoteTagEntity::class,
            parentColumn = "note_id",
            entityColumn = "tag_id"
        ),
        entity = TagEntity::class
    )
    val tags: List<TagEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "note_id",
        entity = ReminderEntity::class
    )
    val reminders: List<ReminderEntity>
)