package com.example.noteapp.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes-table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "note-title")
    val title: String,

    @ColumnInfo(name = "note-description")
    val description: String?,

    @ColumnInfo(name = "note-cat")
    val category: String?,

    @ColumnInfo(name = "note-time")
    val time: String?,

    @ColumnInfo(name = "pinned")
    val pinned: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)