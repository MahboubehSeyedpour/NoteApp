package com.example.noteapp.fakes

import com.example.noteapp.data.local.note.NoteEntity
import com.example.noteapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNoteRepository(
    initial: List<NoteEntity> = emptyList()
) : NoteRepository {

    private val notesFlow = MutableStateFlow(initial.sortedByDescending { it.createdAt })

    override fun getAllNotes(): Flow<List<NoteEntity>> = notesFlow

    override suspend fun addNote(note: NoteEntity) {
        val cur = notesFlow.value.toMutableList()
        cur.add(note)
        notesFlow.value = cur.sortedByDescending { it.createdAt }
    }

    override suspend fun updateNote(note: NoteEntity) {
        val cur = notesFlow.value.toMutableList()
        val idx = cur.indexOfFirst { it.id == note.id }
        if (idx >= 0) cur[idx] = note
        notesFlow.value = cur.sortedByDescending { it.createdAt }
    }

    override suspend fun deleteNote(note: NoteEntity) {
        val cur = notesFlow.value.toMutableList()
        cur.removeAll { it.id == note.id }
        notesFlow.value = cur.sortedByDescending { it.createdAt }
    }

    override fun getNoteById(id: Long): Flow<NoteEntity> =
        notesFlow.map { list -> list.first { it.id == id } }

    override suspend fun getLastNoteId(): Long? {
        TODO("Not yet implemented")
    }

    fun snapshot(): List<NoteEntity> = notesFlow.value
    fun setAll(list: List<NoteEntity>) { notesFlow.value = list.sortedByDescending { it.createdAt } }
}
