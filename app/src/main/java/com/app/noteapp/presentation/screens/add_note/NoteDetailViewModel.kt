package com.app.noteapp.presentation.screens.add_note

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.R
import com.app.noteapp.core.time.combineToEpochMillis
import com.app.noteapp.core.time.formatReminderEpoch
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.reminders.ReminderScheduler
import com.app.noteapp.domain.usecase.NoteUseCase
import com.app.noteapp.domain.usecase.TagUseCase
import com.app.noteapp.presentation.mapper.toNote
import com.app.noteapp.presentation.mapper.toNoteUiModel
import com.app.noteapp.presentation.mapper.toTag
import com.app.noteapp.presentation.mapper.toTagUiMapper
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.TagUiModel
import com.app.noteapp.presentation.model.ToastType
import com.app.noteapp.presentation.theme.ReminderTagColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteUseCase: NoteUseCase,
    private val tagUseCase: TagUseCase,
    private val scheduler: ReminderScheduler,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val noteId: Long? = savedStateHandle["id"]

    val tags: StateFlow<List<TagUiModel>> =
        tagUseCase.getAllTags().map { list -> list.map { it.toTagUiMapper() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())

    private val _uiState =
        MutableStateFlow(NoteDetailUIState(isLoading = false, note = NoteUiModel()))
    val uiState: StateFlow<NoteDetailUIState> = _uiState

    private val _events = MutableSharedFlow<NoteDetailEvents>()
    val events = _events.asSharedFlow()


    init {
        viewModelScope.launch {
            if (noteId != null && noteId != 0L) {
                noteUseCase.getNoteById(noteId).map { it?.toNoteUiModel() }.collect { ui ->
                    _uiState.update { it.copy(isLoading = false, note = ui) }
                }
            }
        }
    }

    fun updateTitle(newTitle: String) = update { it.copy(title = newTitle) }
    fun updateDescription(newDesc: String) = update { it.copy(description = newDesc) }

    fun setReminder(
        dateMillis: Long, hour: Int, minute: Int, zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val epoch = combineToEpochMillis(dateMillis, hour, minute, zoneId)
        val formatted = formatReminderEpoch(epoch, zoneId)

        val reminderTag = TagUiModel(
            id = -100L, name = formatted, color = Color(ReminderTagColor.value)
        )

        mutate { cur ->
            cur.copy(
                reminderAt = epoch,
                timeBadge = formatReminderEpoch(epoch, zoneId),
                reminderTag = reminderTag
            )
        }
    }

    fun onClearReminder() =
        mutate { it.copy(reminderAt = null, timeBadge = null, reminderTag = null) }

    private inline fun mutate(block: (NoteUiModel) -> NoteUiModel) {
        val cur = _uiState.value.note ?: return
        _uiState.update { it.copy(note = block(cur)) }
    }

    private inline fun update(block: (NoteUiModel) -> NoteUiModel) {
        val current = _uiState.value.note ?: return
        _uiState.update { it.copy(note = block(current)) }
    }

    private fun saveNote() {
        if (_uiState.value.note?.title?.isBlank() == true || _uiState.value.note?.title?.isEmpty() == true || _uiState.value.note?.description?.isBlank() == true || _uiState.value.note?.description?.isEmpty() == true) {
            viewModelScope.launch {
                _events.emit(
                    NoteDetailEvents.ShowToast(
                        messageRes = R.string.empty_note_error, type = ToastType.ERROR
                    )
                )
            }
        } else {
            val ui = _uiState.value.note ?: return
            viewModelScope.launch(io) {
                runCatching {
                    scheduler.cancel(ui.id)              // cancel previous
                    val note = ui.toNote()
                    noteUseCase.addOrUpdateNote(note)
                    ui.reminderAt?.let {
                        scheduler.schedule(ui.id, it, ui.title, ui.description)
                    }

                }.onSuccess {
                    _events.emit(NoteDetailEvents.NavigateToHomeScreen)
                }.onFailure { e ->
                    // e.message
                    _events.emit(
                        NoteDetailEvents.ShowToast(
                            messageRes = R.string.fail_to_save_note, type = ToastType.ERROR
                        )
                    )
                }
            }
        }
    }

    fun onBackClicked() {
        if (_uiState.value.note?.title?.isNotBlank() == true || _uiState.value.note?.title?.isNotEmpty() == true || _uiState.value.note?.description?.isNotBlank() == true || _uiState.value.note?.description?.isNotEmpty() == true) {
            saveNote()
        } else viewModelScope.launch { _events.emit(NoteDetailEvents.NavigateToHomeScreen) }
    }

    fun onDeleteClicked() {
        viewModelScope.launch { _events.emit(NoteDetailEvents.RequestDeleteConfirm) }
    }

    fun onConfirmDelete() {
        val id = noteId
        if (id == null) {
            viewModelScope.launch {
                _events.emit(
                    NoteDetailEvents.ShowToast(
                        messageRes = R.string.invalid_note_id, type = ToastType.ERROR
                    )
                )
            }
            return
        }
        viewModelScope.launch(io) {
            runCatching {
                val note = noteUseCase.getNoteById(id).firstOrNull()
                note?.let {
                    noteUseCase.deleteById(note.id)
                }
            }.onSuccess {
                _events.emit(NoteDetailEvents.NavigateToHomeScreen)
            }.onFailure { e ->
//                _events.emit(NoteDetailEvents.Error("Failed to delete: ${e.message}"))
                _events.emit(
                    NoteDetailEvents.ShowToast(
                        messageRes = R.string.fail_to_save_note, type = ToastType.ERROR
                    )
                )
            }
        }
    }

    fun openReminderPicker() = viewModelScope.launch {
        _events.emit(NoteDetailEvents.OpenReminderPicker)
    }

    fun setTag(tag: TagUiModel) {
        _uiState.update { it.copy(selectedTag = tag) }
        update { it.copy(tag = tag) }
    }

    fun addTag(name: String, color: Color) {
        val newTag = TagUiModel(id = 0L, name.trim().lowercase(), color)
        if (newTag.name.equals("all", ignoreCase = true)) {
            viewModelScope.launch {
                _events.emit(
                    NoteDetailEvents.ShowToast(
                        messageRes = R.string.tag_is_reserved, type = ToastType.ERROR
                    )
                )
            }
            return
        }
        viewModelScope.launch(io) {
            tagUseCase.addTag(newTag.toTag())
        }
    }

    fun deleteTag(tagId: Long) {
        viewModelScope.launch(io) {
            runCatching {
                tagUseCase.deleteTagById(tagId)
            }.onSuccess {
                val cur = _uiState.value.note
                if (cur?.tag?.id == tagId) {
                    _uiState.update {
                        it.copy(
                            note = cur.copy(
                                tag = null, timeBadge = cur.timeBadge
                            )
                        )
                    }
                }
            }.onFailure { e ->
                viewModelScope.launch {
//                    _events.emit(NoteDetailEvents.Error("Failed to delete tag: ${e.message}"))
                    _events.emit(
                        NoteDetailEvents.ShowToast(
                            messageRes = R.string.fail_to_save_note, type = ToastType.ERROR
                        )
                    )
                }
            }
        }
    }
}