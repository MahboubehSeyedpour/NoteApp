package com.example.noteapp.presentation.screens.add_note

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.core.time.combineToEpochMillis
import com.example.noteapp.core.time.formatReminderEpoch
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.model.Note
import com.example.noteapp.domain.model.Tag
import com.example.noteapp.domain.reminders.ReminderScheduler
import com.example.noteapp.domain.repository.NoteRepository
import com.example.noteapp.presentation.mapper.toDomain
import com.example.noteapp.presentation.screens.note_details.NoteDetailEvents
import com.example.noteapp.presentation.screens.note_details.models.NoteDetailUIState
import com.example.noteapp.presentation.theme.Primary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val scheduler: ReminderScheduler,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags

    private val _uiState = MutableStateFlow(
        NoteDetailUIState(
            isLoading = false,
            note = Note(0, "", "", null, null, null, 0L, false)
        )
    )
    val uiState: StateFlow<NoteDetailUIState> = _uiState

    private val _events = MutableSharedFlow<NoteDetailEvents>()
    val events = _events.asSharedFlow()

    fun updateTitle(newTitle: String) = update { it.copy(title = newTitle) }
    fun updateDescription(newDesc: String) = update { it.copy(description = newDesc) }

    init {
        _tags.value = listOf(
            Tag("Urgent", Color(0xFFF44336)),
            Tag("Work", Color(0xFF2196F3)),
            Tag("Personal", Color(0xFF4CAF50)),
            Tag("Ideas", Color(0xFFFFC107)),
            Tag("Learning", Color(0xFF9C27B0)),
        )
    }

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
        if (_uiState.value.note?.title?.isBlank() == true ||
            _uiState.value.note?.title?.isEmpty() == true ||
            _uiState.value.note?.description?.isBlank() == true ||
            _uiState.value.note?.description?.isEmpty() == true
        ) {
            viewModelScope.launch {
                _events.emit(NoteDetailEvents.Error("Note must have title and description!"))
            }
        } else {
            val ui = _uiState.value.note ?: return
            viewModelScope.launch(io) {
                runCatching {
                    scheduler.cancel(ui.id)              // cancel previous
                    noteRepository.addNote(ui.toDomain())       // persist
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
    }

    fun onBackClicked() {
        viewModelScope.launch { _events.emit(NoteDetailEvents.NavigateToHomeScreen) }
    }
}