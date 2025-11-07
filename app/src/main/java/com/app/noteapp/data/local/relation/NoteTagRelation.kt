package com.app.noteapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.entity.TagEntity

data class NoteTagRelation(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "id"
    )
    val tag: TagEntity? // nullable when tag_id is null or tag deleted (SET_NULL)
)