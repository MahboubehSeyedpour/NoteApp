package com.app.noteapp.presentation.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import androidx.compose.ui.text.font.FontWeight
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
import com.app.noteapp.presentation.common.components.CustomNoteCard
import com.app.noteapp.presentation.common.components.NotesFilterSheet
import com.app.noteapp.presentation.common.components.ToastHost
import com.app.noteapp.presentation.common.components.TopBar
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.ToastUI
import com.app.noteapp.presentation.model.iconRes
import com.app.noteapp.presentation.navigation.Screens
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController, viewModel: HomeViewModel = hiltViewModel()
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    remember(uiState.notes) { uiState.notes.filter { it.pinned } }
    remember(uiState.notes) { uiState.notes.filterNot { it.pinned } }

    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }
    rememberDrawerState(DrawerValue.Closed)
    rememberCoroutineScope()

    val gridState = rememberLazyStaggeredGridState()
    val locale = LocalConfiguration.current.locales[0]
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }

    var activeDialog by remember { mutableStateOf<AppDialogSpec?>(null) }
    var toastMessage by remember { mutableStateOf<ToastUI?>(null) }


    // ---------------------- Events ----------------------
    LaunchedEffect(viewModel.events) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is HomeEvents.NavigateToNoteDetailScreen -> navController.navigate(
                        route = "${Screens.NoteDetailScreen.route}?id=${event.noteId}"
                    )

                    is HomeEvents.NavigateToSettingsScreen -> navController.navigate(Screens.Settings.route)

                    is HomeEvents.Error -> Toast.makeText(
                        context, event.message, Toast.LENGTH_SHORT
                    ).show()

                    is HomeEvents.RequestDeleteConfirm -> {
                        activeDialog = AppDialogSpec(
                            type = DialogType.ERROR,
                            messageRes = R.string.delete_note_question,
                            confirmTextRes = R.string.delete,
                            dismissTextRes = R.string.dialog_dismiss_btn,
                            onConfirm = {
                                viewModel.confirmDelete(confirmDeleteId ?: 0)
                                activeDialog = null
                            },
                            onDismiss = {
                                activeDialog = null
                            })
                    }

                    is HomeEvents.ShowToast -> {
                        toastMessage = ToastUI(
                            message = event.messageRes, type = event.type
                        )
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
            ), onDismiss = { toastMessage = null }, modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.toast_content_h_padding),
                    vertical = dimensionResource(R.dimen.list_items_v_padding)
                )
        )
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
            TopBar(
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
        NotesContent(
            uiState = uiState,
            inner = inner,
            gridState = gridState,
            onNoteClicked = { noteId -> viewModel.navigateToNoteDetails(noteId) },
            onNotePinned = { noteId -> viewModel.pinNote(noteId) },
            onConfirmDelete = { noteId -> confirmDeleteId = noteId })
    }
}

@Composable
private fun PinnedHeader(
    pinned: NoteUiModel,
    hasOthers: Boolean,
    isSelected: Boolean,
    onNoteClicked: () -> Unit,
    onNotePinned: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = dimensionResource(R.dimen.list_items_v_padding))
    ) {
        CustomNoteCard(
            note = pinned,
            isSelected = isSelected,
            onNoteClicked = onNoteClicked,
            onNotePinned = onNotePinned,
            deleteNote = onDelete,
            noteTitleStyle = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            noteBodyStyle = MaterialTheme.typography.bodySmall,
        )

        if (hasOthers) {
            Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))

            HorizontalDivider(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.v_space_min)),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun NotesContent(
    uiState: HomeUiState,
    inner: PaddingValues,
    gridState: LazyStaggeredGridState,
    onNoteClicked: (Long) -> Unit,
    onNotePinned: (Long) -> Unit,
    onConfirmDelete: (Long) -> Unit
) {
    val pinned = uiState.notes.firstOrNull { it.pinned }
    val others = uiState.notes.filterNot { it.pinned }

    when (uiState.layoutMode) {
        LayoutMode.LIST -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(horizontal = dimensionResource(R.dimen.list_items_h_padding)),
                contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.list_items_v_padding)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_v_padding))
            ) {

                if (pinned != null) {
                    stickyHeader(key = "pinned_header") {
                        PinnedHeader(
                            pinned = pinned,
                            hasOthers = others.isNotEmpty(),
                            isSelected = pinned.id in uiState.selectedIds,
                            onNoteClicked = { onNoteClicked(pinned.id) },
                            onNotePinned = { onNotePinned(pinned.id) },
                            onDelete = { onConfirmDelete(pinned.id) })
                    }
                }

                items(others, key = { it.id }) { note ->
                    CustomNoteCard(
                        note = note,
                        isSelected = note.id in uiState.selectedIds,
                        onNoteClicked = { onNoteClicked(note.id) },
                        onNotePinned = { onNotePinned(note.id) },
                        deleteNote = { onConfirmDelete(note.id) },
                        noteTitleStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        noteBodyStyle = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        LayoutMode.GRID -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(horizontal = dimensionResource(R.dimen.list_items_h_padding))
            ) {

                if (pinned != null) {
                    PinnedHeader(
                        pinned = pinned,
                        hasOthers = others.isNotEmpty(),
                        isSelected = pinned.id in uiState.selectedIds,
                        onNoteClicked = { onNoteClicked(pinned.id) },
                        onNotePinned = { onNotePinned(pinned.id) },
                        onDelete = { onConfirmDelete(pinned.id) })
                }

                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    state = gridState,
                    columns = StaggeredGridCells.Adaptive(minSize = dimensionResource(R.dimen.grid_min_size)),
                    verticalItemSpacing = dimensionResource(R.dimen.list_items_v_padding),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding)),
                    contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.list_items_v_padding))
                ) {
                    items(items = others, key = { it.id }) { note ->
                        CustomNoteCard(
                            note = note,
                            isSelected = note.id in uiState.selectedIds,
                            onNoteClicked = { onNoteClicked(note.id) },
                            onNotePinned = { onNotePinned(note.id) },
                            deleteNote = { onConfirmDelete(note.id) },
                            noteTitleStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            noteBodyStyle = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
