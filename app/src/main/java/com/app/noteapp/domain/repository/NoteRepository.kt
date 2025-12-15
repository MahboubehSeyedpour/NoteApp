package com.app.noteapp.domain.repository

import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.relation.NoteTagRelation
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesStream(): Flow<List<NoteEntity>>
    suspend fun addNote(noteEntity: NoteEntity): Long
    suspend fun updateNote(noteEntity: NoteEntity)
    suspend fun deleteNote(noteEntity: NoteEntity)
    fun getNoteStream(id: Long): Flow<NoteEntity?>
    suspend fun getLastNoteId(): Long?
    fun getNoteWithTagStream(id: Long): Flow<NoteTagRelation>
    fun getNotesWithTagStream(): Flow<List<NoteTagRelation>>
    fun getNotesBetweenStream(start: Long, end: Long): Flow<List<NoteEntity>>
}