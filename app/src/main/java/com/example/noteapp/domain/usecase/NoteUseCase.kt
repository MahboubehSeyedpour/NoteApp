package com.example.noteapp.domain.usecase

import android.content.Context
import com.example.noteapp.data.local.entity.NoteEntity
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.repository.NoteRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        val note = noteRepository.getNoteById(id).first()
        noteRepository.deleteNote(note)
    }

    fun getNoteById(id: Long): Flow<NoteEntity> = noteRepository.getNoteById(id)

    suspend fun getLastNoteId(): Long? = noteRepository.getLastNoteId()
}
