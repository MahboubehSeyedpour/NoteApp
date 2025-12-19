package com.app.noteapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.relation.NoteTagRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(noteEntity: NoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertForImport(noteEntity: NoteEntity): Long

    @Transaction
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getNotesStream(): Flow<List<NoteTagRelation>>

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Transaction
    @Query("SELECT * FROM notes WHERE id=:id")
    fun getNoteStream(id: Long): Flow<NoteTagRelation?>

    @Query("SELECT id FROM `notes` ORDER BY id DESC LIMIT 1")
    fun getLastNoteId(): Long?

    @Query("""
    SELECT * FROM notes
    WHERE created_at >= :start AND created_at < :end
""")
    fun getNotesBetweenStream(start: Long, end: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteTagRelation?

    @Query("SELECT id FROM notes")
    suspend fun getAllIds(): List<Long>

}