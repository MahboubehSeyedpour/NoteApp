package com.app.noteapp.domain.repository

import com.app.noteapp.data.local.model.enums.MediaKind
import com.app.noteapp.domain.model.common_model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    // ---- CRUD ----
    suspend fun addNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)

    suspend fun getLastNoteId(): Long?
    suspend fun getAllIds(): List<Long>

    // ---- Streams
    fun getNotesStream(): Flow<List<Note>>
    fun getNoteStream(id: Long): Flow<Note?>
    fun getNotesBetweenStream(start: Long, end: Long): Flow<List<Note>>

    // ---- Tags junction ----
    suspend fun attachTag(
        noteId: Long,
        tagId: Long,
        createdAt: Long = System.currentTimeMillis()
    )

    suspend fun detachTag(noteId: Long, tagId: Long)
    suspend fun detachAllTags(noteId: Long)

    suspend fun appendMediaBlock(
        noteId: Long,
        kind: MediaKind,
        localUri: String
    )
}
