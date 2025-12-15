package com.app.noteapp.data.repository

import com.app.noteapp.data.local.dao.NoteDao
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.local.relation.NoteTagRelation
import com.app.noteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getNotesStream(): Flow<List<NoteEntity>> = noteDao.getNotesStream()

    override suspend fun addNote(noteEntity: NoteEntity): Long = noteDao.addNote(noteEntity)

    override suspend fun updateNote(noteEntity: NoteEntity) = noteDao.updateNote(noteEntity)

    override suspend fun deleteNote(noteEntity: NoteEntity) = noteDao.deleteNote(noteEntity)

    override fun getNoteStream(id: Long): Flow<NoteEntity?> = noteDao.getNoteStream(id)

    override suspend fun getLastNoteId(): Long? = noteDao.getLastNoteId()

    override fun getNotesWithTagStream(): Flow<List<NoteTagRelation>> = noteDao.getNotesWithTagStream()

    override fun getNoteWithTagStream(id: Long): Flow<NoteTagRelation> = noteDao.getNoteWithTagStream(id)

    override fun getNotesBetweenStream(start: Long, end: Long): Flow<List<NoteEntity>> =
        noteDao.getNotesBetweenStream(start, end)
}