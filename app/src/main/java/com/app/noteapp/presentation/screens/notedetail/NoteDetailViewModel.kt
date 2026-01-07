package com.app.noteapp.presentation.screens.notedetail

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.R
import com.app.noteapp.data.local.model.enums.MediaKind
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.reminders.ReminderScheduler
import com.app.noteapp.domain.usecase.NoteUseCase
import com.app.noteapp.domain.usecase.TagUseCase
import com.app.noteapp.presentation.mapper.toDomain
import com.app.noteapp.presentation.mapper.toUi
import com.app.noteapp.presentation.model.NoteBlockUiModel
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.TagUiModel
import com.app.noteapp.presentation.model.ToastType
import com.app.noteapp.presentation.screens.notedetail.contract.NoteDetailEffect
import com.app.noteapp.presentation.screens.notedetail.contract.NoteDetailEvent
import com.app.noteapp.presentation.screens.notedetail.contract.NoteDetailState
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
import kotlin.collections.get

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteUseCase: NoteUseCase,
    private val tagUseCase: TagUseCase,
    private val scheduler: ReminderScheduler,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val noteId: Long? = savedStateHandle["id"]

    private val _events = MutableSharedFlow<NoteDetailEvent>()
    val events = _events.asSharedFlow()

    val tags: StateFlow<List<TagUiModel>> =
        tagUseCase.getAllTags()
            .map { list -> list.map { it.toUi() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(
        NoteDetailState(
            isLoading = true,
            note = null
        )
    )
    val uiState: StateFlow<NoteDetailState> = _uiState

    init {
        viewModelScope.launch {
            if (noteId != null && noteId != 0L) {
                noteUseCase.getNoteById(noteId)
                    .map { it?.toUi() }
                    .collect { ui ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                note = ui
                            )
                        }
                    }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        note = NoteUiModel(
                            id = 0L,
                            userId = 0L,
                            directoryId = null,
                            title = "",
                            pinned = false,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis(),
                            deletedAt = null,
                            blocks = emptyList(),
                            tags = emptyList(),
                            reminders = emptyList()
                        )
                    )
                }
            }
        }
    }

    private inline fun mutateNote(block: (NoteUiModel) -> NoteUiModel) {
        val cur = _uiState.value.note ?: return
        _uiState.update { it.copy(note = block(cur)) }
    }

    // ---------- Title ----------

    fun updateTitle(newTitle: String) {
        mutateNote { it.copy(title = newTitle) }

        val note = _uiState.value.note ?: return
        if (note.id > 0) {
            viewModelScope.launch(io) {
                noteUseCase.update(note.toDomain())
            }
        }
    }

    // ---------- Blocks: Text ----------

    fun addTextBlockAtEnd(initialText: String = "") {
        val note = _uiState.value.note ?: return
        if (note.id <= 0L) {
            return
        }

        viewModelScope.launch(io) {
            noteUseCase.addTextBlock(
                noteId = note.id,
                position = null,
                initialText = initialText
            )
        }
    }

    fun updateTextBlock(blockId: Long, newText: String) {
        mutateNote { note ->
            val updatedBlocks = note.blocks.map { block ->
                when (block) {
                    is NoteBlockUiModel.Text ->
                        if (block.id == blockId) block.copy(text = newText) else block
                    else -> block
                }
            }
            note.copy(blocks = updatedBlocks)
        }

        // Persist
        viewModelScope.launch(io) {
            noteUseCase.updateTextBlock(blockId, newText)
        }
    }

    fun deleteBlock(blockId: Long) {
        mutateNote { note ->
            note.copy(
                blocks = note.blocks.filterNot { it.id == blockId }
            )
        }

        viewModelScope.launch(io) {
            noteUseCase.deleteBlock(blockId)
        }
    }

    fun moveBlock(fromIndex: Int, toIndex: Int) {
        val note = _uiState.value.note ?: return
        val blocks = note.blocks
        if (fromIndex !in blocks.indices || toIndex !in blocks.indices) return
        if (fromIndex == toIndex) return

        val mutable = blocks.toMutableList()
        val block = mutable.removeAt(fromIndex)
        mutable.add(toIndex, block)
        mutateNote { it.copy(blocks = mutable) }

        viewModelScope.launch(io) {
            val fromPos = blocks[fromIndex].position
            val toPos = blocks[toIndex].position
            noteUseCase.moveBlock(
                noteId = note.id,
                fromPosition = fromPos,
                toPosition = toPos
            )
        }
    }

    fun setReminder(
        dateMillis: Long,
        hour: Int,
        minute: Int,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        // TODO
    }

    fun saveNote(onDone: suspend () -> Unit) {
        val note = _uiState.value.note ?: return

        if (note.title.isBlank() && note.blocks.isEmpty()) {
            viewModelScope.launch { onDone() }
            return
        }

        viewModelScope.launch(io) {
            runCatching {
                noteUseCase.addOrUpdateNote(note.toDomain())
            }.onSuccess {
                onDone()
            }.onFailure {
                _events.emit(NoteDetailEvent.SaveFailed)
            }
        }
    }

    fun backClicked() {
        saveNote {
            _events.emit(NoteDetailEvent.NavigateHome)
        }
    }

    fun deleteNote() = viewModelScope.launch (io){
        _events.emit(NoteDetailEvent.DeleteConfirmationRequired)
    }

    fun confirmDelete() {
        val id = noteId
        if (id == null || id == 0L) {
            viewModelScope.launch { _events.emit(NoteDetailEvent.DeleteFailed) }
            return
        }
        viewModelScope.launch(io) {
            runCatching {
                val note = noteUseCase.getNoteById(id).firstOrNull()
                note?.let {
                    noteUseCase.deleteById(note.id)
                }
            }.onSuccess {
                _events.emit(NoteDetailEvent.NavigateHome)
            }.onFailure {
                _events.emit(NoteDetailEvent.DeleteFailed)
            }
        }
    }

//    fun openReminderPicker() = viewModelScope.launch {
//        _events.emit(NoteDetailEvents.OpenReminderPicker)
//    }

    fun setTag(tag: TagUiModel) {
        // TODO
    }

    fun addTag(name: String, color: Color) {
        // TODO
    }

    fun deleteTag(tagId: Long) {
        // TODO
    }

    fun onImagePicked(uri: Uri?) {
        val currentNote = _uiState.value.note ?: return
        if (currentNote.id <= 0L) {
            return
        }

        viewModelScope.launch {
            noteUseCase.appendMediaBlock(
                noteId = currentNote.id,
                kind = MediaKind.IMAGE,
                localUri = uri.toString()
            )
            val updated = noteUseCase.getNoteById(currentNote.id).firstOrNull()
            if (updated != null) {
                _uiState.update { it.copy(note = updated.toUi()) }
            }
        }
    }

    fun changeEditMode(mode: Boolean) {
        _uiState.update { it.copy(editMode = mode) }
    }
}
