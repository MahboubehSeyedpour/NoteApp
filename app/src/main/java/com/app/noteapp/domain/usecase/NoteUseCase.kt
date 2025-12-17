package com.app.noteapp.domain.usecase

import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.domain.repository.NoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) {

    fun getAllNotes(): Flow<List<Note>> = noteRepository.getNotesStream()

    suspend fun addOrUpdateNote(note: Note) {
        withContext(io) {
            if (note.id == 0L) noteRepository.addNote(note)
            else noteRepository.updateNote(note)
        }
    }

    suspend fun update(note: Note) = withContext(io) {
        noteRepository.updateNote(note)
    }

    suspend fun deleteById(id: Long) = withContext(io) {
        val note = noteRepository.getNoteStream(id).firstOrNull()
        note?.let {
            noteRepository.deleteNote(note)
        }
    }

    fun getNoteById(id: Long): Flow<Note?> = noteRepository.getNoteStream(id)

    fun getNotesBetween(start: Long, end: Long): Flow<List<Note>> =
        noteRepository.getNotesBetweenStream(start, end)
}
