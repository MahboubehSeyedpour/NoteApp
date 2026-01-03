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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.presentation.components.CustomAlertDialog
import com.app.noteapp.presentation.components.CustomFab
import com.app.noteapp.presentation.components.CustomNoteCard
import com.app.noteapp.presentation.components.NotesFilterSheet
import com.app.noteapp.presentation.components.TopBar
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.iconRes
import com.app.noteapp.presentation.navigation.Screens
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
//    currentFont: AppFont,
//    onFontSelected: (AppFont) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    remember(uiState.notes) { uiState.notes.filter { it.pinned } }
    remember(uiState.notes) { uiState.notes.filterNot { it.pinned } }

    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val gridState = rememberLazyStaggeredGridState()
    val locale = LocalConfiguration.current.locales[0]
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }


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
                }
            }
        }
    }


    // ---------------------- Locale ----------------------
//    LaunchedEffect(uiState.language) {
//        val locales = when (uiState.language) {
//            Language.FA -> LocaleListCompat.forLanguageTags("fa")
//            Language.EN -> LocaleListCompat.forLanguageTags("en")
//        }
//        AppCompatDelegate.setApplicationLocales(locales)
//    }


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

    // ---------------------- Confirm delete dialog ----------------------
    if (confirmDeleteId != null) {
        CustomAlertDialog(
            type = DialogType.ERROR,
            onDismissRequest = { confirmDeleteId = null },
            message = stringResource(R.string.delete_note_question),
            onConfirmBtnClick = {
                confirmDeleteId?.let { id -> viewModel.deleteNote(id) }
                confirmDeleteId = null
            },
            confirmBtnText = R.string.dialog_delete_confirm_btn,
            onDismissButtonClick = { confirmDeleteId = null },
            dismissBtnText = R.string.dialog_dismiss_btn,
            showTopBar = true
        )
    }


    // ---------------------- Import launcher ----------------------
//    val importLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.OpenDocument()
//    ) { uri ->
//        if (uri == null) return@rememberLauncherForActivityResult
//
//        scope.launch {
//            runCatching {
//                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
//                    ?: error("Failed to open input stream")
//
//                viewModel.importBackup(bytes).collect { result ->
//                    result.onSuccess { res ->
//                        viewModel.clearFilters()
//                        viewModel.changeSearchQuery("")
//                        Toast.makeText(
//                            context,
//                            "Imported: ${res.notesImported} notes, ${res.tagsImported} tags",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }.onFailure { e ->
//                        Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }.onFailure { e ->
//                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    // ---------------------- Export launcher ----------------------
//    val exportLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.CreateDocument("application/json")
//    ) { uri ->
//        if (uri == null) return@rememberLauncherForActivityResult
//
//        scope.launch {
//            runCatching {
//                val bytes = viewModel.exportNotesBytes()
//                context.contentResolver.openOutputStream(uri, "w")?.use { out ->
//                    out.write(bytes)
//                    out.flush()
//                } ?: error("Failed to open output stream")
//            }.onFailure { e ->
//                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
//            }.onSuccess {
//                Toast.makeText(context, "Exported", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }


//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            DrawerContent(
//                currentAvatar = uiState.avatar,
//                onAvatarSelected = { type ->
//                    viewModel.changeAvatar(type)
//                    scope.launch { drawerState.close() }
//                },
//                currentLanguage = uiState.language, onLanguageSelected = { newLang ->
//                    viewModel.changeLanguage(newLang)
//                    scope.launch { drawerState.close() }
//                },
////              currentFont = currentFont,
////              onFontSelected = onFontSelected,
//              onExportClicked = {
////                exportLauncher.launch("noteapp-backup-${System.currentTimeMillis()}.json")
//                scope.launch { drawerState.close() }
//            },
//                onImportClicked = {
////                  importLauncher.launch(arrayOf("application/json"))
//                    scope.launch { drawerState.close() }
//                }
//            )
//        }
//    ) {
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
                    onAvatarClick = {
                        viewModel.onAvatarClicked()
//                        scope.launch { drawerState.open() }
                                    },
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
//    }
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
            Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

            HorizontalDivider(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.v_space)),
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
