package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.noteapp.data.local.entity.ReminderEntity

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Query("UPDATE reminders SET deleted_at = :now, updated_at = :now, dirty = 1 WHERE id = :id")
    suspend fun softDelete(id: Long, now: Long)

    @Query("""
        SELECT * FROM reminders
        WHERE note_id = :noteId AND deleted_at IS NULL
        ORDER BY trigger_at ASC
    """)
    fun getRemindersStream(noteId: Long): kotlinx.coroutines.flow.Flow<List<ReminderEntity>>
}