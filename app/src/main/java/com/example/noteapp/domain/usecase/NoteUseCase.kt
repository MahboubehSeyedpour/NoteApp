package com.example.noteapp.domain.usecase

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.noteapp.data.local.note.NoteEntity
import com.example.noteapp.data.repository.NoteRepositoryImpl
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.repository.NoteRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
