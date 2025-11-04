package com.example.noteapp.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.noteapp.data.local.entity.NoteEntity
import com.example.noteapp.data.local.entity.TagEntity

data class NoteTagRelation(
    @Embedded val note: NoteEntity,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "id"
    )
    val tag: TagEntity? // nullable when tag_id is null or tag deleted (SET_NULL)
)