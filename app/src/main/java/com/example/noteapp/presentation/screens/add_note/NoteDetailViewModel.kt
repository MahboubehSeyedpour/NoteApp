package com.example.noteapp.presentation.screens.add_note

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.core.time.combineToEpochMillis
import com.example.noteapp.core.time.formatReminderEpoch
import com.example.noteapp.di.IoDispatcher
import com.example.noteapp.domain.model.Note
import com.example.noteapp.domain.model.Tag
import com.example.noteapp.domain.reminders.ReminderScheduler
import com.example.noteapp.domain.usecase.NoteUseCase
import com.example.noteapp.domain.usecase.TagUseCase
import com.example.noteapp.presentation.mapper.toDomain
import com.example.noteapp.presentation.mapper.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteUseCase: NoteUseCase,
    private val tagUseCase: TagUseCase,
    private val scheduler: ReminderScheduler,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val noteId: Long? = savedStateHandle.get<Long?>("id")

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags

    private val _uiState = MutableStateFlow(
        NoteDetailUIState(
            isLoading = false, note = Note(
                id = 0,
                title = "",
                description = "",
                timeBadge = null,
                reminderAt = null,
                createdAt = 0L,
                pinned = false,
                tag = null
            )
        )
    )
    val uiState: StateFlow<NoteDetailUIState> = _uiState

    private val _events = MutableSharedFlow<NoteDetailEvents>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            if (noteId != null && noteId != 0L) {
                noteUseCase.getNoteById(noteId).map {
                    if (it.tagId == null) it.toUI(null) else it.toUI(
                        tagUseCase.getTag(it.tagId).first().toUI()
                    )
                }.collect { ui ->
                    _uiState.update { it.copy(isLoading = false, note = ui) }
                }
            }

        }
        observeTags()
    }

    private fun observeTags() {
        viewModelScope.launch(io) {
            tagUseCase.getAllTags().collectLatest { allTags ->
                _tags.value = allTags.map { it.toUI() }
            }
        }
    }


    fun updateTitle(newTitle: String) = update { it.copy(title = newTitle) }
    fun updateDescription(newDesc: String) = update { it.copy(description = newDesc) }
//    fun updateCategory(newCategory: String?) = update { it.copy(categoryBadge = newCategory) }
//    fun updateTimeBadge(newTime: String?) = update { it.copy(timeBadge = newTime) }

    fun setReminder(
        dateMillis: Long, hour: Int, minute: Int, zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val epoch = combineToEpochMillis(dateMillis, hour, minute, zoneId)
        mutate { cur ->
            cur.copy(
                reminderAt = epoch, timeBadge = formatReminderEpoch(epoch, zoneId)
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

    private fun saveNote() {
        if (_uiState.value.note?.title?.isBlank() == true || _uiState.value.note?.title?.isEmpty() == true || _uiState.value.note?.description?.isBlank() == true || _uiState.value.note?.description?.isEmpty() == true) {
            viewModelScope.launch {
                _events.emit(NoteDetailEvents.Error("Note must have title and description!"))
            }
        } else {
            val ui = _uiState.value.note ?: return
            viewModelScope.launch(io) {
                runCatching {
                    scheduler.cancel(ui.id)              // cancel previous
                    if (noteId == null || noteId == 0L) {
                        noteUseCase.addOrUpdateNote(ui.toDomain())
                    } else {
                        noteUseCase.addOrUpdateNote(ui.toDomain())
                    }
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
        if (_uiState.value.note?.title?.isNotBlank() == true || _uiState.value.note?.title?.isNotEmpty() == true || _uiState.value.note?.description?.isNotBlank() == true || _uiState.value.note?.description?.isNotEmpty() == true) {
            saveNote()
        } else viewModelScope.launch { _events.emit(NoteDetailEvents.NavigateToHomeScreen) }
    }

    fun applyQuickReminder(option: QuickReminder) {
        val now = ZonedDateTime.now()
        val target = when (option) {
            QuickReminder.LATER_TODAY -> now.withHour(18).withMinute(0).withSecond(0)
                .withNano(0)  // 6 PM today
            QuickReminder.TOMORROW_MORNING -> now.plusDays(1).withHour(9).withMinute(0)
                .withSecond(0).withNano(0) // 9 AM tomorrow
        }
        val millis = target.toInstant().toEpochMilli()
        _uiState.update { it.copy(reminderAtMillis = millis) }
        setReminder(millis)
    }

    fun openReminderPicker() = viewModelScope.launch {
        _events.emit(NoteDetailEvents.OpenReminderPicker)
    }

    fun setReminder(dateMillis: Long, hour: Int? = null, minute: Int? = null) {
        val finalMillis = if (hour != null && minute != null) {
            Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).withHour(hour)
                .withMinute(minute).withSecond(0).withNano(0).toInstant().toEpochMilli()
        } else dateMillis

        _uiState.update { it.copy(reminderAtMillis = finalMillis) }

        // TODO: persist reminder with the note, schedule alarm/WorkManager if needed
        // viewModelScope.launch(io) { repo.updateReminder(noteId, finalMillis) }
    }

    fun onTagSelected(tag: Tag) {
        _uiState.update { it.copy(selectedTag = tag) }
        update { it.copy(tag = tag) }
//        _uiState.update { s -> s.copy(note = s.note?.copy(categoryBadge = tag.name)) }
    }

    fun onAddTag(name: String, color: Color) {
        val newTag = Tag(id = 0L, name.trim().lowercase(), color)
        if (newTag.name.equals("all", ignoreCase = true)) {
            viewModelScope.launch { _events.emit(NoteDetailEvents.Error("Tag name 'All' is reserved")) }
            return
        }
        viewModelScope.launch(io) {
            tagUseCase.addTag(newTag.toDomain())
        }
    }
}