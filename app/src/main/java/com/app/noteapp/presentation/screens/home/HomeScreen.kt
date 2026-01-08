package com.app.noteapp.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListPrefetchStrategy
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.presentation.common.components.CustomAlertDialog
import com.app.noteapp.presentation.common.components.CustomFab
import com.app.noteapp.presentation.common.components.NoteCard
import com.app.noteapp.presentation.common.components.NotesFilterSheet
import com.app.noteapp.presentation.common.components.ToastHost
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.ToastUI
import com.app.noteapp.presentation.model.iconRes
import com.app.noteapp.presentation.navigation.Screens
import com.app.noteapp.presentation.screens.home.contract.HomeEffect
import com.app.noteapp.presentation.screens.home.contract.HomeUiActions
import com.app.noteapp.presentation.screens.home.mapper.HomeEffectMapper
import com.app.noteapp.presentation.screens.home.ui.component.HomeTopBar
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController, viewModel: HomeViewModel = hiltViewModel()
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LocalContext.current

    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    remember(uiState.notes) { uiState.notes.filter { it.pinned } }
    remember(uiState.notes) { uiState.notes.filterNot { it.pinned } }
    rememberDrawerState(DrawerValue.Closed)
    rememberCoroutineScope()

    val gridState = rememberSaveable(
        saver = LazyStaggeredGridState.Saver
    ) {
        LazyStaggeredGridState()
    }

    val locale = LocalConfiguration.current.locales[0]
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }

    var activeDialog by remember { mutableStateOf<AppDialogSpec?>(null) }
    var toastMessage by remember { mutableStateOf<ToastUI?>(null) }
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }

    val notes by remember(uiState.notes) {
        derivedStateOf { uiState.notes }
    }

    val pinnedNotes by remember(notes) {
        derivedStateOf { notes.filter { it.pinned } }
    }

    val normalNotes by remember(notes) {
        derivedStateOf { notes.filterNot { it.pinned } }
    }

    val actions = remember {
        HomeUiActions(onConfirmDelete = { id ->
            viewModel.confirmDelete(id)
            activeDialog = null
        }, onDismissDialog = {
            activeDialog = null
        })
    }

    val mapper = remember { HomeEffectMapper(actions) }

    // ---------------------- Events ----------------------
    LaunchedEffect(viewModel.events) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->

                val effect = mapper.map(event)

                when (effect) {

                    is HomeEffect.NavigateToNoteDetail -> {
                        navController.navigate(
                            route = "${Screens.NoteDetailScreen.route}?id=${effect.noteId}"
                        )
                    }

                    HomeEffect.NavigateToSettings -> {
                        navController.navigate(Screens.Settings.route)
                    }

                    is HomeEffect.Toast -> {
                        toastMessage = ToastUI(
                            message = effect.message, type = effect.type
                        )
                    }

                    is HomeEffect.Dialog -> {
                        activeDialog = effect.spec.copy(
                            onConfirm = {
                                confirmDeleteId?.let(actions.onConfirmDelete)
                            })
                    }
                }
            }
        }
    }

    activeDialog?.let { dialog ->
        CustomAlertDialog(spec = dialog)
    }

    toastMessage?.let { t ->
        ToastHost(
            toast = ToastUI(
                message = t.message, type = t.type
            ), onDismiss = { toastMessage = null })
    }

    // ---------------------- Filter sheet ----------------------
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            NotesFilterSheet(
                tags = uiState.tags,
                selectedTagId = uiState.selectedTagId,
                onFilterClicked = { tagId, rangeStart, rangeEnd, onlyReminders ->
                    showFilterSheet = false
                    viewModel.filterNotes(tagId, rangeStart, rangeEnd, onlyReminders)
                },
                rangeStart = uiState.rangeStart,
                rangeEnd = uiState.rangeEnd,
                onCustomRangeSelected = viewModel::filterCustomRange,
                onlyReminder = uiState.onlyReminder,
                onOnlyReminderChange = viewModel::filterNotesWithReminder,
                onClose = { showFilterSheet = false },
                onDeleteAllFiltersClicked = {
                    showFilterSheet = false
                    viewModel.clearFilters()
                })
        }
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.screen_padding)),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar(
                notesSize = uiState.notes.size,
                locale = locale,
                avatar = painterResource(uiState.avatar.iconRes()),
                onAvatarClick = { viewModel.onAvatarClicked() },
                query = uiState.searchQuery,
                onSearchChange = viewModel::changeSearchQuery,
                onGridToggleClicked = viewModel::toggleList,
                layoutMode = uiState.layoutMode,
                onFilterClick = { showFilterSheet = true },
                onSortClick = { viewModel.onSortByDateClicked() },
                isFilterActive = uiState.isFilterActive
            )
        },
        floatingActionButton = { CustomFab({ viewModel.addNote() }) },
        floatingActionButtonPosition = FabPosition.End
    ) { inner ->
        Notes(
            modifier = Modifier.padding(inner),
            layoutMode = uiState.layoutMode,
            pinnedNotes = pinnedNotes,
            normalNotes = normalNotes,
            selectedIds = uiState.selectedIds,
            gridState = gridState,
            onOpen = { noteId -> viewModel.navigateToNoteDetails(noteId) },
            onPin = { noteId -> viewModel.pinNote(noteId) },
            onDelete = { noteId -> viewModel.confirmDelete(noteId) },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notes(
    modifier: Modifier,
    layoutMode: LayoutMode,
    pinnedNotes: List<NoteUiModel>,
    normalNotes: List<NoteUiModel>,
    selectedIds: Set<Long>,
    gridState: LazyStaggeredGridState,
    onOpen: (Long) -> Unit,
    onPin: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {

    val listState = rememberLazyListState(
        prefetchStrategy = LazyListPrefetchStrategy(
            nestedPrefetchItemCount = 6
        )
    )
    AnimatedContent(
        targetState = layoutMode, transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }, label = "layout-animation"
    ) { mode ->

        when (mode) {

            LayoutMode.LIST -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensionResource(R.dimen.list_items_h_padding)),
                    state = listState,
                    contentPadding = PaddingValues(
                        bottom = dimensionResource(R.dimen.list_items_v_padding)
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.list_items_v_padding)
                    )
                ) {

                    item(key = "pinned-section") {
                        PinnedNotesSection(
                            pinnedNotes = pinnedNotes,
                            showDivider = normalNotes.isNotEmpty(),
                            selectedIds = selectedIds,
                            onOpen = onOpen,
                            onPin = onPin,
                            onDelete = onDelete,
                        )
                    }

                    items(
                        items = normalNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            isSelected = note.id in selectedIds,
                            noteDetails = { onOpen(note.id) },
                            pinNote = { onPin(note.id) },
                            deleteNote = { onDelete(note.id) },
                        )
                    }
                }
            }

            LayoutMode.GRID -> {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensionResource(R.dimen.list_items_h_padding))
                ) {

                    PinnedNotesSection(
                        pinnedNotes = pinnedNotes,
                        showDivider = normalNotes.isNotEmpty(),
                        selectedIds = selectedIds,
                        onOpen = onOpen,
                        onPin = onPin,
                        onDelete = onDelete,
                    )

                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        state = gridState,
                        columns = StaggeredGridCells.Adaptive(
                            minSize = dimensionResource(R.dimen.grid_min_size)
                        ),
                        verticalItemSpacing = dimensionResource(R.dimen.list_items_v_padding),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.list_items_h_padding)
                        ),
                        contentPadding = PaddingValues(
                            bottom = dimensionResource(R.dimen.list_items_v_padding)
                        )
                    ) {
                        items(
                            items = normalNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                isSelected = note.id in selectedIds,
                                noteDetails = { onOpen(note.id) },
                                pinNote = { onPin(note.id) },
                                deleteNote = { onDelete(note.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinnedNotesSection(
    pinnedNotes: List<NoteUiModel>,
    showDivider: Boolean,
    selectedIds: Set<Long>,
    onOpen: (Long) -> Unit,
    onPin: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    if (pinnedNotes.isEmpty()) return

    pinnedNotes.forEach { note ->
        NoteCard(
            note = note,
            isSelected = note.id in selectedIds,
            noteDetails = { onOpen(note.id) },
            pinNote = { onPin(note.id) },
            deleteNote = { onDelete(note.id) },
        )
    }

    if (showDivider) {
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space_mid)))
        HorizontalDivider(color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space_mid)))
    }
}
