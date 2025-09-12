package com.example.noteapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.mapper.toUI
import com.example.noteapp.domain.repository.NoteRepository
import com.example.noteapp.presentation.model.NoteUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val _events = MutableSharedFlow<HomeEvents>()
    val events = _events.asSharedFlow()

    val notes: StateFlow<List<NoteUI>> =
        noteRepository.getAllNotes()
            .map { list -> list.sortedByDescending { it.createdAt }.map { it.toUI() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun onNoteDetailsClicked(noteId: Long) = viewModelScope.launch {
        _events.emit(
            HomeEvents.NavigateToNoteDetailsScreen(noteId)
        )
    }
}