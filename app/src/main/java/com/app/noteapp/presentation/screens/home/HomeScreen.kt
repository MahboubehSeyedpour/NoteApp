package com.app.noteapp.presentation.screens.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.core.extensions.toLocalizedDigits
import com.app.noteapp.domain.common_model.AppFont
import com.app.noteapp.domain.common_model.AppLanguage
import com.app.noteapp.presentation.components.CustomAlertDialog
import com.app.noteapp.presentation.components.CustomFab
import com.app.noteapp.presentation.components.CustomNoteCard
import com.app.noteapp.presentation.components.NotesFilterSheet
import com.app.noteapp.presentation.components.TopBar
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.model.iconRes
import com.app.noteapp.presentation.navigation.Screens
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    currentFont: AppFont,
    onFontSelected: (AppFont) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val layoutMode by viewModel.layoutMode.collectAsState()
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }
    val query by viewModel.query.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val avatar by viewModel.avatar.collectAsState()
    val lang by viewModel.language.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val pinnedNote = notes.filter { it.pinned }
    val others = notes.filterNot { it.pinned }
    val gridState = rememberLazyStaggeredGridState()
    val locale = LocalConfiguration.current.locales[0]
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    val isFilterActive by viewModel.onFilter.collectAsState()
    val rangeStart by viewModel.rangeStart.collectAsState()
    val rangeEnd by viewModel.rangeEnd.collectAsState()

    LaunchedEffect(viewModel.events) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is HomeEvents.NavigateToNoteDetailScreen -> navController.navigate(
                        route = "${Screens.NoteDetailScreen.route}?id=${event.noteId}"
                    )

                    is HomeEvents.Error -> Toast.makeText(
                        context, event.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(lang) {
        val locales = when (lang) {
            AppLanguage.FA -> LocaleListCompat.forLanguageTags("fa")
            AppLanguage.EN -> LocaleListCompat.forLanguageTags("en")
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            NotesFilterSheet(
                tags = tags,
                selectedTagId = viewModel.selectedTagId.collectAsState().value,
                onFilterClicked = { tagId, rangeStart, rangeEnd, onlyReminders ->
                    showFilterSheet = false
                    viewModel.onFilterClicked(tagId, rangeStart, rangeEnd, onlyReminders)
                },
                rangeStart = rangeStart,
                rangeEnd = rangeEnd,
                onCustomRangeSelected = viewModel::filterCustomRange,
                onlyReminder = viewModel.onlyReminder.collectAsStateWithLifecycle().value,
                onOnlyReminderChange = viewModel::filterNotesWithReminder,
                onClose = { showFilterSheet = false },
                onDeleteAllFiltersClicked = {
                    showFilterSheet = false
                    viewModel.clearFilters()
                })
        }
    }

    if (confirmDeleteId != null) {
        CustomAlertDialog(
            type = DialogType.ERROR,
            onDismissRequest = { confirmDeleteId = null },
            title = stringResource(R.string.delete_note),
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


    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        scope.launch {
            runCatching {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: error("Failed to open input stream")

                viewModel.importBackup(bytes).collect { result ->
                    result.onSuccess { res ->
                        viewModel.clearFilters()
                        viewModel.onSearchQueryChange("")
                        Toast.makeText(
                            context,
                            "Imported: ${res.notesImported} notes, ${res.tagsImported} tags",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.onFailure { e ->
                        Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.onFailure { e ->
                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        scope.launch {
            runCatching {
                val bytes = viewModel.exportNotesBytes()
                context.contentResolver.openOutputStream(uri, "w")?.use { out ->
                    out.write(bytes)
                    out.flush()
                } ?: error("Failed to open output stream")
            }.onFailure { e ->
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }.onSuccess {
                Toast.makeText(context, "Exported", Toast.LENGTH_SHORT).show()
            }
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentAvatar = avatar,
                onAvatarSelected = { type ->
                viewModel.changeAvatar(type)
                scope.launch { drawerState.close() } },
                currentLanguage = lang,
                onLanguageSelected = { newLang ->
                viewModel.changeLanguage(newLang)
                scope.launch { drawerState.close() } },
                currentFont = currentFont,
                onFontSelected = onFontSelected,
                onExportClicked = {
                        exportLauncher.launch("noteapp-backup-${System.currentTimeMillis()}.json")
                        scope.launch { drawerState.close() }
                },
                onImportClicked = {importLauncher.launch(arrayOf("application/json")) }
            )
        }) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.screen_padding)),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopBar(
                    avatar = painterResource(avatar.iconRes()),
                    onAvatarClick = { scope.launch { drawerState.open() } },
                    query = query,
                    onSearchChange = viewModel::onSearchQueryChange,
                    onGridToggleClicked = viewModel::onGridToggleClicked,
                    layoutMode = layoutMode,
                    onFilterClick = { showFilterSheet = true },
                    isFilterActive = isFilterActive
                )
            },
            bottomBar = {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.btm_sheet_v_padding)),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = notes.size.toLocalizedDigits(locale).plus(" ")
                            .plus(stringResource(R.string.notes)),
                        textAlign = TextAlign.Center
                    )
                }
            },
            floatingActionButton = {
                CustomFab({ viewModel.addNote() })
            },
            floatingActionButtonPosition = FabPosition.End
        ) { inner ->

            when (layoutMode) {
                LayoutMode.LIST -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner)
                            .padding(horizontal = dimensionResource(R.dimen.list_items_h_padding)),
                        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.list_items_v_padding)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_v_padding))
                    ) {
                        items(pinnedNote, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.navigateToNoteDetails(note.id) },
                                pinNote = { viewModel.onPinNoteClicked(note.id) },
                                deleteNote = { confirmDeleteId = note.id },
                                noteTitleStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                                ),
                                noteBodyStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            )
                        }

                        if (pinnedNote.isNotEmpty() && others.isNotEmpty()) {
                            item(key = "pinned-divider") {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.v_space)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        items(others, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.navigateToNoteDetails(note.id) },
                                pinNote = { viewModel.onPinNoteClicked(note.id) },
                                deleteNote = { confirmDeleteId = note.id },
                                noteTitleStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                                ),
                                noteBodyStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            )
                        }
                    }
                }

                LayoutMode.GRID -> {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner)
                            .padding(horizontal = dimensionResource(R.dimen.list_items_h_padding)),
                        state = gridState,
                        columns = StaggeredGridCells.Adaptive(minSize = dimensionResource(R.dimen.grid_min_size)),
                        verticalItemSpacing = dimensionResource(R.dimen.list_items_v_padding),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding)),
                        contentPadding = PaddingValues(bottom = dimensionResource(R.dimen.list_items_v_padding))
                    ) {

                        items(pinnedNote, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.navigateToNoteDetails(note.id) },
                                pinNote = { viewModel.onPinNoteClicked(note.id) },
                                deleteNote = { confirmDeleteId = note.id },
                                noteTitleStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                                ),
                                noteBodyStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            )
                        }

                        if (pinnedNote.isNotEmpty() && others.isNotEmpty()) {
                            item(
                                key = "pinned-divider", span = StaggeredGridItemSpan.FullLine
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.v_space)),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        items(others, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.navigateToNoteDetails(note.id) },
                                pinNote = { viewModel.onPinNoteClicked(note.id) },
                                deleteNote = { confirmDeleteId = note.id },
                                noteTitleStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                                ),
                                noteBodyStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            )
                        }
                    }
                }
            }
        }
    }
}
