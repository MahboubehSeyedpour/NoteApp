package com.example.noteapp.data.local.note

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM `notes-table`")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)

    @Query("SELECT * FROM `notes-table` WHERE id=:id")
    fun getNoteById(id: Int): Flow<NoteEntity>

    @Query("SELECT id FROM `notes-table` ORDER BY id DESC LIMIT 1")
    fun getLastNoteId(): Long?
}