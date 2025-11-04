package com.example.noteapp.data.local.note

import androidx.room.Embedded
import androidx.room.Relation
import com.example.noteapp.data.local.tag.TagEntity

data class NoteTagRelation(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "id"
    )
    val tag: TagEntity? // nullable when tag_id is null or tag deleted (SET_NULL)
)
