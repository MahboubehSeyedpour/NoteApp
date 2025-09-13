package com.example.noteapp.domain.repository

import com.example.noteapp.data.local.note.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun addNote(noteEntity: NoteEntity)
    suspend fun updateNote(noteEntity: NoteEntity)
    suspend fun deleteNote(noteEntity: NoteEntity)
    fun getNoteById(id: Int): Flow<NoteEntity>
    suspend fun getLastNoteId(): Long?
}