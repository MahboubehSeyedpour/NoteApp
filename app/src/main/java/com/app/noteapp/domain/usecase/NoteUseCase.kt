package com.app.noteapp.domain.usecase

import android.content.Context
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.repository.NoteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val io: CoroutineDispatcher
) {

    fun getAllNotes(): Flow<List<NoteEntity>> = noteRepository.getAllNotes()

    suspend fun addOrUpdateNote(note: NoteEntity) {
        withContext(io) {
            if (note.id == 0L) noteRepository.addNote(note)
            else noteRepository.updateNote(note)
        }
    }

    suspend fun update(note: NoteEntity) = withContext(io) {
        noteRepository.updateNote(note)
    }

    suspend fun deleteById(id: Long) = withContext(io) {
        val note = noteRepository.getNoteById(id).firstOrNull()
        note?.let {
            noteRepository.deleteNote(note)
        }
    }

    fun getNoteById(id: Long): Flow<NoteEntity?> = noteRepository.getNoteById(id)

    suspend fun getLastNoteId(): Long? = noteRepository.getLastNoteId()

    fun getNotesBetween(start: Long, end: Long): Flow<List<NoteEntity>> =
        noteRepository.getNotesBetween(start, end)
}
