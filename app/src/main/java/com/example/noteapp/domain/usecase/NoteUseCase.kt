package com.example.noteapp.domain.usecase

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.noteapp.data.local.note.NoteEntity
import com.example.noteapp.data.repository.NoteRepositoryImpl
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val coroutineScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    private val noteRepository: NoteRepositoryImpl,
) {
    var notes: List<NoteEntity> by mutableStateOf(emptyList())
        private set

    private var observeKeysJob: Job? = null

    fun observe() {
        observeNotes()
    }

    private fun observeNotes() {
        observeKeysJob?.cancel()
        observeKeysJob = coroutineScope.launch {
            getAllNotes().collectLatest { notes ->
            }
        }
    }

    private fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteRepository.getAllNotes()
    }

    private suspend fun addNote(noteEntity: NoteEntity) {
        if (noteEntity.id == 0L) {
            noteRepository.addNote(noteEntity)
        } else {
            noteRepository.updateNote(noteEntity)
        }
    }

    fun pinNote(note: NoteEntity) {
        coroutineScope.launch(NonCancellable + Dispatchers.IO) {
            addNote(note)
        }
    }

    fun deleteNoteById(id: Long) {
        coroutineScope.launch(NonCancellable + Dispatchers.IO) {
            val noteToDelete = noteRepository.getNoteById(id).first()
            noteRepository.deleteNote(noteToDelete)
        }
    }

    fun getNoteById(id: Long): Flow<NoteEntity> {
        return noteRepository.getNoteById(id)
    }

    fun getLastNoteId(onResult: (Long?) -> Unit) {
        coroutineScope.launch(NonCancellable + Dispatchers.IO) {
            val lastNoteId = noteRepository.getLastNoteId()
            withContext(Dispatchers.Main) {
                onResult(lastNoteId)
            }
        }
    }
}