package com.example.noteapp.presentation.screens.note_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.core.time.combineToEpochMillis
import com.example.noteapp.core.time.formatReminderEpoch
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.model.Note
import com.example.noteapp.domain.reminders.ReminderScheduler
import com.example.noteapp.domain.repository.NoteRepository
import com.example.noteapp.presentation.mapper.toDomain
import com.example.noteapp.presentation.mapper.toUI
import com.example.noteapp.presentation.screens.note_details.models.NoteDetailUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject


@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    private val scheduler: ReminderScheduler,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val noteId: Long = checkNotNull(savedStateHandle.get<Long>("id"))

    private val _uiState = MutableStateFlow(NoteDetailUIState())
    val uiState: StateFlow<NoteDetailUIState> = _uiState

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

    fun setReminder(
        dateMillis: Long,
        hour: Int,
        minute: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val epoch = combineToEpochMillis(dateMillis, hour, minute, zoneId)
        mutate { cur ->
            cur.copy(
                reminderAt = epoch,
                timeBadge = formatReminderEpoch(epoch, zoneId)
            )
        }
    }

    fun clearReminder() = mutate { it.copy(reminderAt = null, timeBadge = null) }

    private inline fun mutate(block: (Note) -> Note) {
        val cur = _uiState.value.note ?: return
        _uiState.update { it.copy(note = block(cur)) }
    }

    private inline fun update(block: (Note) -> Note) {
        val current = _uiState.value.note ?: return
        _uiState.update { it.copy(note = block(current)) }
    }

    fun onDoneClicked() {
        val ui = _uiState.value.note ?: return
        viewModelScope.launch(io) {
            runCatching {
                scheduler.cancel(ui.id)              // cancel previous
                noteRepository.updateNote(ui.toDomain())       // persist
                ui.reminderAt?.let {
                    scheduler.schedule(ui.id, it, ui.title, ui.description)
                }
            }.onSuccess {
                _events.emit(NoteDetailEvents.NavigateToHomeScreen)
            }.onFailure { e ->
                _events.emit(NoteDetailEvents.Error("Failed to save note: ${e.message}"))
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch { _events.emit(NoteDetailEvents.NavigateToHomeScreen) }
    }
}
