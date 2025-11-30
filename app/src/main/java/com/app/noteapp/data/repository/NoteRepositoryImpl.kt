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
    override fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    override suspend fun addNote(noteEntity: NoteEntity) = noteDao.addNote(noteEntity)

    override suspend fun updateNote(noteEntity: NoteEntity) = noteDao.updateNote(noteEntity)

    override suspend fun deleteNote(noteEntity: NoteEntity) = noteDao.deleteNote(noteEntity)

    override fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    override suspend fun getLastNoteId(): Long? = noteDao.getLastNoteId()

    override fun getAllNotesWithTag(): Flow<List<NoteTagRelation>> = noteDao.getAllNotesWithTag()

    override fun getNoteWithTagById(id: Long): Flow<NoteTagRelation> = noteDao.getNoteWithTagById(id)

    override fun getNotesBetween(start: Long, end: Long): Flow<List<NoteEntity>> =
        noteDao.getNotesBetween(start, end)
}