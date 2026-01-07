package com.app.noteapp.presentation.screens.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.core.time.TimeRange
import com.app.noteapp.core.time.rangeFor
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.model.common_model.Note
import com.app.noteapp.domain.usecase.AppPreferencesUseCase
import com.app.noteapp.domain.usecase.ExportNotesUseCase
import com.app.noteapp.domain.usecase.ImportNotesUseCase
import com.app.noteapp.domain.usecase.NoteUseCase
import com.app.noteapp.domain.usecase.TagUseCase
import com.app.noteapp.presentation.mapper.toUi
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.SortOrder
import com.app.noteapp.presentation.model.TagUiModel
import com.app.noteapp.presentation.model.ToastType
import com.app.noteapp.presentation.screens.add_note.NoteDetailEvents
import com.app.noteapp.presentation.screens.settings.SettingsUiState
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
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val appPreferencesUseCase: AppPreferencesUseCase,
    private val exportNotesUseCase: ExportNotesUseCase,
    private val importNotesUseCase: ImportNotesUseCase,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val _events = MutableSharedFlow<HomeEvents>()
    val events = _events.asSharedFlow()

    private val zoneId: ZoneId = ZoneId.systemDefault()

    val preferencesState: StateFlow<SettingsUiState> =
        appPreferencesUseCase()
            .map { s ->
                SettingsUiState(
                    isLoading = false,
                    themeMode = s.themeMode,
                    language = s.language,
                    font = s.fontPref,
                    avatar = s.avatar
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState(isLoading = true)
            )

    private val _homeUiState = MutableStateFlow(
        HomeUiState(
            sortOrder = SortOrder.DESC,
            layoutMode = LayoutMode.LIST,
            searchQuery = "",
            selectedTagId = ALL_TAG_ID,
            onlyReminder = false,
            timeFilter = TimeFilter.ALL,
            rangeStart = null,
            rangeEnd = null,
            selectedIds = emptySet(),
        )
    )

    private val tagsFlow: StateFlow<List<TagUiModel>> =
        tagUseCase.getAllTags()
            .map { list -> list.map { it.toUi() } }
            .map { ui ->
                val withoutAll = ui.filterNot { it.id == ALL_TAG_ID || it.name.equals("all", ignoreCase = true) }
                listOf(ALL_TAG) + withoutAll
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = listOf(ALL_TAG)
            )

    // -------- base state: local + (language/avatar/tags) --------
    private val baseState: StateFlow<HomeUiState> =
        combine(
            _homeUiState,
            preferencesState,
            tagsFlow
        ) { s, prefs, tags ->
            s.copy(
                language = prefs.language,
                avatar = prefs.avatar,
                tags = tags
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val notesFlow: Flow<List<NoteUiModel>> =
        baseState
            .map { s ->
                if (!s.isFilterActive) null
                else rangeFor(s.timeFilter, s.rangeStart, s.rangeEnd, zoneId)
            }
            .distinctUntilChanged()
            .flatMapLatest { range: TimeRange? ->
                val src: Flow<List<Note>> = if (range == null) {
                    noteUseCase.getAllNotes()
                } else {
                    noteUseCase.getNotesBetween(range.start, range.endExclusive)
                }

                src.map { list -> list.map { it.toUi() } }
            }

    val homeUiState: StateFlow<HomeUiState> =
        combine(
            baseState,
            notesFlow
        ) { s, notes ->
            s.copy(notes = notes)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = baseState.value
        )

    val tags: StateFlow<List<TagUiModel>> =
        baseState
            .map { it.tags }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = baseState.value.tags
            )

    fun onSortByDateClicked() {
        _homeUiState.update { s ->
            s.copy(
                sortOrder = if (s.sortOrder == SortOrder.DESC) SortOrder.ASC else SortOrder.DESC
            )
        }
    }


    fun addNote() = viewModelScope.launch {
        _events.emit(HomeEvents.NavigateToNoteDetailScreen(null))
    }

    fun toggleList() {
        _homeUiState.update { s ->
            s.copy(layoutMode = if (s.layoutMode == LayoutMode.LIST) LayoutMode.GRID else LayoutMode.LIST)
        }
    }

    fun deleteNote() = viewModelScope.launch (io){
        _events.emit(HomeEvents.RequestDeleteConfirm)
    }

    fun confirmDelete(id: Long) {
        if (id == 0L) {
            viewModelScope.launch {
                _events.emit(
                    HomeEvents.ShowToast(
                        messageRes = R.string.invalid_note_id,
                        type = ToastType.ERROR
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
            }.onFailure {
                _events.emit(
                    HomeEvents.ShowToast(
                        messageRes = R.string.fail_to_delete_note,
                        type = ToastType.ERROR
                    )
                )
            }
        }
    }

    fun pinNote(id: Long) {
        viewModelScope.launch(io) {
            runCatching {
                // Snapshot from state (UI list)
                val currentNotes = homeUiState.value.notes
                val pinnedId = currentNotes.firstOrNull { it.pinned }?.id
                val togglingOff = (pinnedId == id)

                // Unpin existing pinned note (if any)
                if (pinnedId != null) {
                    val pinnedDomain = noteUseCase.getNoteById(pinnedId).firstOrNull()
                    if (pinnedDomain != null) {
                        noteUseCase.update(pinnedDomain.copy(pinned = false))
                    }
                }

                // Pin target unless toggling off
                if (!togglingOff) {
                    val target = noteUseCase.getNoteById(id).firstOrNull()
                    if (target != null) {
                        noteUseCase.update(target.copy(pinned = true))
                    }
                }
            }.onFailure {
                _events.emit(HomeEvents.Error("Failed to pin: ${it.message}"))
            }
        }
    }

//    suspend fun exportNotesBytes(): ByteArray = exportNotesUseCase()
//
//    fun importBackup(bytes: ByteArray): Flow<Result<ImportResult>> =
//        flow { emit(runCatching { importNotesUseCase(bytes) }) }.flowOn(io)

    // ============================================= navigation =============================================
    fun navigateToNoteDetails(noteId: Long) = viewModelScope.launch {
        _events.emit(HomeEvents.NavigateToNoteDetailScreen(noteId))
    }

    // ============================================= search & filter =============================================
    fun changeSearchQuery(newQuery: String) {
        _homeUiState.update { it.copy(searchQuery = newQuery) }
    }

    fun filterNotes(tagId: Long?, rangeStart: Long?, rangeEnd: Long?, onlyReminders: Boolean) {
        _homeUiState.update { s ->
            val normalizedTagId = tagId ?: ALL_TAG_ID
            val validRange = (rangeStart != null && rangeEnd != null && rangeStart <= rangeEnd)

            s.copy(
                selectedTagId = normalizedTagId,
                onlyReminder = onlyReminders,
                timeFilter = if (validRange) TimeFilter.CUSTOM_RANGE else TimeFilter.ALL,
                rangeStart = if (validRange) rangeStart else null,
                rangeEnd = if (validRange) rangeEnd else null
            )
        }
    }

    fun filterCustomRange(start: Long?, end: Long?) {
        _homeUiState.update { s ->
            s.copy(timeFilter = TimeFilter.CUSTOM_RANGE, rangeStart = start, rangeEnd = end)
        }
    }

    fun filterNotesWithReminder(enabled: Boolean) {
        _homeUiState.update { it.copy(onlyReminder = enabled) }
    }

    fun clearFilters() = _homeUiState.update {
        it.copy(
            selectedTagId = ALL_TAG_ID,
            onlyReminder = false,
            timeFilter = TimeFilter.ALL,
            rangeStart = null,
            rangeEnd = null
        )
    }

    // ============================================= settings =============================================\
//    fun changeAvatar(type: AvatarType) = viewModelScope.launch(io) {
////        avatarUseCase(type)
//    }
//
//    fun changeLanguage(lang: Language) = viewModelScope.launch {
////        languageUseCase(lang)
//    }

    fun onAvatarClicked(){
        viewModelScope.launch {
            _events.emit(HomeEvents.NavigateToSettingsScreen)
        }
    }

}

const val ALL_TAG_ID: Long = -1L
val ALL_TAG = TagUiModel(
    id = ALL_TAG_ID, name = "All", color = Color(ReminderTagColor.value)
)

enum class TimeFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH, CUSTOM_RANGE
}