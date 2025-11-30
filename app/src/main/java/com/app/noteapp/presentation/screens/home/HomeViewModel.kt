package com.app.noteapp.presentation.screens.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.core.time.TimeRange
import com.app.noteapp.core.time.rangeFor
import com.app.noteapp.data.local.entity.NoteEntity
import com.app.noteapp.data.mapper.toDomain
import com.app.noteapp.data.mapper.toUI
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.model.AppLanguage
import com.app.noteapp.domain.model.AvatarType
import com.app.noteapp.domain.model.Note
import com.app.noteapp.domain.model.Tag
import com.app.noteapp.domain.usecase.AvatarTypeUseCase
import com.app.noteapp.domain.usecase.LanguageUseCase
import com.app.noteapp.domain.usecase.NoteUseCase
import com.app.noteapp.domain.usecase.TagUseCase
import com.app.noteapp.presentation.theme.ReminderTagColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteUseCase: NoteUseCase,
    private val tagUseCase: TagUseCase,
    private val avatarUseCase: AvatarTypeUseCase,
    private val languageUseCase: LanguageUseCase,
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

    private val _timeFilter = MutableStateFlow(TimeFilter.ALL)
    val timeFilter: StateFlow<TimeFilter> = _timeFilter

    private val _rangeStart = MutableStateFlow<Long?>(null) // epoch millis
    val rangeStart: StateFlow<Long?> = _rangeStart

    private val _rangeEnd = MutableStateFlow<Long?>(null)
    val rangeEnd: StateFlow<Long?> = _rangeEnd

    private val _onlyReminder = MutableStateFlow(false)
    val onlyReminder: StateFlow<Boolean> = _onlyReminder

    private val _onFilter = MutableStateFlow(false)
    val onFilter: StateFlow<Boolean> = _onFilter

    val language = languageUseCase().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLanguage.FA
    )

    val avatar: StateFlow<AvatarType> = avatarUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AvatarType.MALE
    )

    val tags: StateFlow<List<Tag>> =
        tagUseCase.getAllTags().map { list -> list.map { it.toUI() } }.map { ui ->
            val withoutAll = ui.filterNot { it.id == ALL_TAG_ID || it.name.equals("all", true) }
            listOf(ALL_TAG) + withoutAll
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf(ALL_TAG))


    private val _selectedTagId = MutableStateFlow<Long>(ALL_TAG_ID)
    val selectedTagId: StateFlow<Long> = _selectedTagId

    private val zoneId: ZoneId = ZoneId.systemDefault()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val filteredNotes: Flow<List<NoteEntity>> =
        combine(
            _timeFilter,
            _rangeStart,
            _rangeEnd,
            _onFilter
        ) { filter, start, end, onFilter ->
            if (!onFilter) {
                null
            } else {
                rangeFor(filter, start, end, zoneId)
            }
        }.flatMapLatest { range: TimeRange? ->
            if (range == null) {
                // no effective date restriction
                noteUseCase.getAllNotes()
            } else {
                noteUseCase.getNotesBetween(range.start, range.endExclusive)
            }
        }

    private val _notes: Flow<List<Note>> = filteredNotes.map { all ->
        all
            .sortedByDescending {
                it.createdAt
            }
            .map { entity ->
                val tagUi = entity.tagId?.let { tid ->
                    tagUseCase.getTag(tid).firstOrNull()?.toUI()
                }
                entity.toUI(tagUi)
            }
    }

    private val baseFilters: Flow<Triple<List<Note>, String, Long>> =
        combine(_notes, _query, selectedTagId) { list, q, tagId ->
            Triple(list, q, tagId)
        }

    val notes: StateFlow<List<Note>> = combine(
        baseFilters,
        onlyReminder
    ) { (list, q, tagId), onlyReminder ->

        val term = q.trim().lowercase()

        val byQuery = if (term.isBlank()) list else list.filter { n ->
            n.title.lowercase().contains(term) ||
                    (n.description ?: "").lowercase().contains(term)
        }

        val byTag = if (tagId == ALL_TAG_ID) byQuery
        else byQuery.filter { it.tag?.id == tagId }

        val byReminder =
            if (!onlyReminder) byTag
            else byTag.filter { it.reminderAt != null }

        byReminder.sortedWith(
            compareByDescending<Note> { it.pinned }
                .thenByDescending { it.createdAt }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )



    fun onAvatarSelected(type: AvatarType) {
        viewModelScope.launch(io) {
            avatarUseCase(type)
        }
    }

    fun onSearchChange(newQuery: String) {
        _query.value = newQuery
    }

    fun clearFilters() {
        _selectedTagId.value = ALL_TAG_ID
        _onlyReminder.value = false
        _timeFilter.value = TimeFilter.ALL
        _rangeStart.value = null
        _rangeEnd.value = null
        _onFilter.value = false
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

    fun deleteNote(id: Long) {
        viewModelScope.launch(io) {
            runCatching {
                val note = noteUseCase.getNoteById(id).firstOrNull()
                note?.let { noteUseCase.deleteById(it.id) }
            }.onFailure {
                viewModelScope.launch {
                    _events.emit(HomeEvents.Error("Failed to delete: ${it.message}"))
                }
            }
        }
    }

    fun pinNote(id: Long) {
        viewModelScope.launch(io) {
            runCatching {
                val current = notes.firstOrNull()
                val currentlyPinned = current?.firstOrNull { it.pinned }

                // Unpin previously pinned note (if different)
                if (currentlyPinned != null && currentlyPinned.id != id) {
                    noteUseCase.update(currentlyPinned.copy(pinned = false).toDomain())
                }

                // Pin target
                val target = noteUseCase.getNoteById(id).firstOrNull()
                target?.let {
                    noteUseCase.update(it.copy(pinned = true))
                }

                // If user taps pin on already pinned note, unpin it
                if (currentlyPinned != null && currentlyPinned.id == id) {
                    noteUseCase.update(currentlyPinned.copy(pinned = false).toDomain())
                }
            }.onFailure {
                viewModelScope.launch {
                    _events.emit(HomeEvents.Error("Failed to pin: ${it.message}"))
                }
            }
        }
    }

    fun onLanguageSelected(lang: AppLanguage) {
        viewModelScope.launch { languageUseCase(lang) }
    }

    fun onOnlyReminderChange(enabled: Boolean) {
        _onlyReminder.value = enabled
    }
    fun onFilterClicked(
        tagId: Long?,
        rangeStart: Long?,
        rangeEnd: Long?,
        onlyReminders: Boolean
    ) {
        // 1) Tag
        _selectedTagId.value = tagId ?: ALL_TAG_ID

        // 2) Reminder only
        _onlyReminder.value = onlyReminders

        // 3) Time range
        if (rangeStart != null && rangeEnd != null && rangeStart <= rangeEnd) {
            _timeFilter.value = TimeFilter.CUSTOM_RANGE
            _rangeStart.value = rangeStart
            _rangeEnd.value = rangeEnd
        } else {
            // No valid range -> fall back to ALL
            _timeFilter.value = TimeFilter.ALL
            _rangeStart.value = null
            _rangeEnd.value = null
        }

        _onFilter.value = true
    }

    fun onCustomRangeSelected(start: Long?, end: Long?) {
        _timeFilter.value = TimeFilter.CUSTOM_RANGE
        _rangeStart.value = start
        _rangeEnd.value = end
    }

}

const val ALL_TAG_ID: Long = -1L
val ALL_TAG = Tag(
    id = ALL_TAG_ID, name = "All", color = Color(ReminderTagColor.value)
)

enum class TimeFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH, CUSTOM_RANGE
}