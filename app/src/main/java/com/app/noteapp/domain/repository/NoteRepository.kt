package com.app.noteapp.domain.repository

import com.app.noteapp.domain.common_model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesStream(): Flow<List<Note>>
    suspend fun addNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    fun getNoteStream(id: Long): Flow<Note?>
    suspend fun getLastNoteId(): Long?
    fun getNotesBetweenStream(start: Long, end: Long): Flow<List<Note>>
}