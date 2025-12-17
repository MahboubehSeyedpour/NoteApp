package com.app.noteapp.data.repository

import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.domain.mapper.toNote
import com.app.noteapp.domain.mapper.toNoteEntity
import com.app.noteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getNotesStream(): Flow<List<Note>> =
        noteDao.getNotesStream().map { list -> list.map { it.toNote() } }

    override suspend fun addNote(note: Note): Long = noteDao.addNote(note.toNoteEntity())

    override suspend fun updateNote(note: Note) = noteDao.updateNote(note.toNoteEntity())

    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note.toNoteEntity())

    override fun getNoteStream(id: Long): Flow<Note?> =
        noteDao.getNoteStream(id).map { it?.toNote() }

    override suspend fun getLastNoteId(): Long? = noteDao.getLastNoteId()

    override fun getNotesBetweenStream(start: Long, end: Long): Flow<List<Note>> =
        noteDao.getNotesBetweenStream(start, end).map { list -> list.map { it.toNote() } }
}