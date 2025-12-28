package com.app.noteapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("note_id"),
        Index("trigger_at"),
        Index(value = ["note_id", "server_row_id"], unique = true)
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,

    @ColumnInfo(name = "note_id") val noteId: Long,
    @ColumnInfo(name = "trigger_at") val triggerAt: Long,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true,

    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "deleted_at") val deletedAt: Long? = null,

    @ColumnInfo(name = "server_row_id") val serverId: String? = null,
    @ColumnInfo(name = "version") val version: Long = 0,
    @ColumnInfo(name = "dirty") val dirty: Boolean = true
)
