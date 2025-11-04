package com.example.noteapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.SET_NULL,   // if tag is deleted, keep note but null the tag
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tag_id")]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "title") val title: String,

    @ColumnInfo(name = "tag_id") val tagId: Long?,

    @ColumnInfo(name = "description") val description: String?,

    @ColumnInfo(name = "reminder_at") val reminderAt: Long?,

    @ColumnInfo(name = "pinned") val pinned: Boolean = false,

    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)