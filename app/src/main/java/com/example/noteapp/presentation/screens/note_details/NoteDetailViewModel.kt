package com.example.noteapp.presentation.screens.note_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.mapper.toDomain
import com.example.noteapp.domain.mapper.toUI
import com.example.noteapp.domain.repository.NoteRepository
import com.example.noteapp.presentation.model.NoteUI
import com.example.noteapp.presentation.screens.note_details.models.NoteDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val noteId: Int = checkNotNull(savedStateHandle.get<Long>("id")).toInt()

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState

    private val _events = MutableSharedFlow<NoteDetailEvents>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            noteRepository.getNoteById(noteId)
                .map { it.toUI() }
                .collect { ui ->
                    _uiState.update { it.copy(isLoading = false, note = ui) }
                }
        }
    }

    fun updateTitle(newTitle: String) = update { it.copy(title = newTitle) }
    fun updateDescription(newDesc: String) = update { it.copy(description = newDesc) }
    fun updateCategory(newCategory: String?) = update { it.copy(categoryBadge = newCategory) }
    fun updateTimeBadge(newTime: String?) = update { it.copy(timeBadge = newTime) }

    private inline fun update(block: (NoteUI) -> NoteUI) {
        val current = _uiState.value.note ?: return
        _uiState.update { it.copy(note = block(current)) }
    }

    fun onDoneClicked() {
        val current = _uiState.value.note ?: return
        viewModelScope.launch(io) {
            _uiState.update { it.copy(isSaving = true) }
            runCatching {
                noteRepository.updateNote(current.toDomain())
            }.onSuccess {
                _events.emit(NoteDetailEvents.NavigateToHomeScreen)
            }.onFailure { e ->
                _events.emit(NoteDetailEvents.Error("Failed to save note: ${e.message}"))
            }.also {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch { _events.emit(NoteDetailEvents.NavigateToHomeScreen) }
    }
}
