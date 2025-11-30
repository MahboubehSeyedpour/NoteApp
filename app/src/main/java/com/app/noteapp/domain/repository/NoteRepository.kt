package com.app.noteapp.domain.repository

import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.relation.NoteTagRelation
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun addNote(noteEntity: NoteEntity)
    suspend fun updateNote(noteEntity: NoteEntity)
    suspend fun deleteNote(noteEntity: NoteEntity)
    fun getNoteById(id: Long): Flow<NoteEntity?>
    suspend fun getLastNoteId(): Long?
    fun getNoteWithTagById(id: Long): Flow<NoteTagRelation>
    fun getAllNotesWithTag(): Flow<List<NoteTagRelation>>
    fun getNotesBetween(start: Long, end: Long): Flow<List<NoteEntity>>
}