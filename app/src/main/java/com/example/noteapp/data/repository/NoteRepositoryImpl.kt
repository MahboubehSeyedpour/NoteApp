package com.example.noteapp.data.repository

import com.example.noteapp.data.local.note.NoteDao
import com.example.noteapp.data.local.note.NoteEntity
import com.example.noteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    override suspend fun addNote(noteEntity: NoteEntity) {
        noteDao.addNote(noteEntity)
    }

    override suspend fun updateNote(noteEntity: NoteEntity) {
        noteDao.updateNote(noteEntity)
    }

    override suspend fun deleteNote(noteEntity: NoteEntity) {
        noteDao.deleteNote(noteEntity)
    }

    override fun getNoteById(id: Int): Flow<NoteEntity> {
        return noteDao.getNoteById(id)
    }

    override suspend fun getLastNoteId(): Long? {
        return noteDao.getLastNoteId()
    }
}