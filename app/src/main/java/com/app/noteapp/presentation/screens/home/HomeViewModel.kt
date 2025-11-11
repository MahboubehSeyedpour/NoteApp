package com.app.noteapp.presentation.screens.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.data.mapper.toDomain
import com.app.noteapp.data.mapper.toUI
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.model.Note
import com.app.noteapp.domain.model.Tag
import com.app.noteapp.domain.usecase.AvatarTypeUseCase
import com.app.noteapp.domain.usecase.NoteUseCase
import com.app.noteapp.domain.usecase.TagUseCase
import com.app.noteapp.presentation.model.AvatarType
import com.app.noteapp.presentation.theme.ReminderTagColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    private val tagUseCase: TagUseCase,
    private val avatarUseCase: AvatarTypeUseCase,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val _events = MutableSharedFlow<HomeEvents>()
    val events = _events.asSharedFlow()

    private val _layoutMode = MutableStateFlow(LayoutMode.LIST)
    val layoutMode: StateFlow<LayoutMode> = _layoutMode

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _selected = MutableStateFlow<Set<Long>>(emptySet())
    val selected: StateFlow<Set<Long>> = _selected

    val avatar: StateFlow<AvatarType> =
        avatarUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AvatarType.MALE
            )
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> =
        tagUseCase.getAllTags().map { list -> list.map { it.toUI() } }.map { ui ->
            val withoutAll = ui.filterNot { it.id == ALL_TAG_ID || it.name.equals("all", true) }
            listOf(ALL_TAG) + withoutAll
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf(ALL_TAG))


    private val _selectedTagId = MutableStateFlow<Long>(ALL_TAG_ID)
    val selectedTagId: StateFlow<Long> = _selectedTagId

    private val _notes: Flow<List<Note>> = noteUseCase.getAllNotes().map { all ->
        all.sortedByDescending { it.createdAt }.map { entity ->
            val tagUi = entity.tagId?.let { tid -> tagUseCase.getTag(tid).firstOrNull()?.toUI() }
            entity.toUI(tagUi)
        }
    }
    val notes: StateFlow<List<Note>> = combine(_notes, _query, _selectedTagId) { list, q, tagId ->
        val term = q.trim().lowercase()
        val byQuery = if (term.isBlank()) list else list.filter { n ->
            n.title.lowercase().contains(term) || (n.description ?: "").lowercase().contains(term)
        }
        val byTag = if (tagId == ALL_TAG_ID) byQuery
        else byQuery.filter { it.tag?.id == tagId }

        byTag.sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.createdAt })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onAvatarSelected(type: AvatarType) {
        viewModelScope.launch(io) {
            avatarUseCase(type)
        }
    }

    fun onSearchChange(newQuery: String) {
        _query.value = newQuery
    }

    fun clearSelection() {
        _selected.value = emptySet()
    }

    fun onNoteDetailsClicked(noteId: Long) = viewModelScope.launch {
        _events.emit(HomeEvents.NavigateToNoteDetailScreen(noteId))
    }

    fun onAddNoteClicked() = viewModelScope.launch {
        _events.emit(HomeEvents.NavigateToNoteDetailScreen(null))
    }

    fun onGridToggleClicked() {
        _layoutMode.update { mode -> if (mode == LayoutMode.LIST) LayoutMode.GRID else LayoutMode.LIST }
    }

    fun deleteSelected() {
        val ids = _selected.value
        if (ids.isEmpty()) return
        viewModelScope.launch(io) {
            runCatching {
                ids.forEach { id ->
                    val note = noteUseCase.getNoteById(id).firstOrNull()
                    note?.let {
                        noteUseCase.deleteById(note.id)
                    }
                }
            }.onSuccess {
                clearSelection()
            }.onFailure {
                viewModelScope.launch { _events.emit(HomeEvents.Error("Failed to delete: ${it.message}")) }
            }
        }
    }

    fun pinSelected() {
        val ids = _selected.value
        if (ids.size != 1) {
            viewModelScope.launch { _events.emit(HomeEvents.Error("Select exactly one note to pin")) }
            return
        }
        val targetId = ids.firstOrNull()
        viewModelScope.launch(io) {
            runCatching {
                val current = notes.firstOrNull()
                val currentlyPinned = current?.firstOrNull { it.pinned }
                if (currentlyPinned != null && currentlyPinned.id != targetId) {
                    noteUseCase.update(currentlyPinned.copy(pinned = false).toDomain())
                }

                targetId?.let {
                    val target = noteUseCase.getNoteById(targetId).first()
                    target?.let {
                        noteUseCase.update(target.copy(pinned = true))
                    }
                }

                if (currentlyPinned != null && currentlyPinned.id == targetId) {
                    noteUseCase.update(currentlyPinned.copy(pinned = false).toDomain())
                }
            }.onSuccess {
                clearSelection()
            }.onFailure {
                viewModelScope.launch { _events.emit(HomeEvents.Error("Failed to pin: ${it.message}")) }
            }
        }
    }

    fun toggleSelection(noteId: Long) {
        _selected.update { cur -> if (noteId in cur) cur - noteId else cur + noteId }
    }

    fun onTagFilterSelected(tag: Tag) {
        _selectedTagId.value = tag.id
    }
}

const val ALL_TAG_ID: Long = -1L
val ALL_TAG = Tag(
    id = ALL_TAG_ID, name = "All", color = Color(ReminderTagColor.value)
)