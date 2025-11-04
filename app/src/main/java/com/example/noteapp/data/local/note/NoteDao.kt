package com.example.noteapp.data.local.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM `notes`")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM `notes` WHERE id=:id")
    fun getNoteById(id: Long): Flow<NoteEntity>

    @Query("SELECT id FROM `notes` ORDER BY id DESC LIMIT 1")
    fun getLastNoteId(): Long?

    @Transaction
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getAllNotesWithTag(): Flow<List<NoteTagRelation>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id=:id")
    fun getNoteWithTagById(id: Long): Flow<NoteTagRelation>


    @Transaction
    @Query("SELECT * FROM notes WHERE id=:tagId ORDER BY created_at DESC")
    fun getNotesByTag(tagId: Long): Flow<List<NoteTagRelation>>

}