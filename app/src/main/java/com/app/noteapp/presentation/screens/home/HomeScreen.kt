package com.app.noteapp.presentation.screens.home

import android.widget.Toast
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
import androidx.compose.ui.unit.dp
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
import com.app.noteapp.domain.model.AppLanguage
import com.app.noteapp.presentation.components.CustomAlertDialog
import com.app.noteapp.presentation.components.CustomFab
import com.app.noteapp.presentation.components.CustomNoteCard
import com.app.noteapp.presentation.components.TagsList
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.model.iconRes
import com.app.noteapp.presentation.navigation.Screens
import com.app.noteapp.presentation.screens.home.components.NotesFilterSheet
import com.app.noteapp.presentation.screens.home.components.TopBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val layoutMode by viewModel.layoutMode.collectAsState()
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }
    val query by viewModel.query.collectAsState()
    val selected by viewModel.selected.collectAsState()
    selected.isNotEmpty()
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

    val timeFilter by viewModel.timeFilter.collectAsState()
    val rangeStart by viewModel.rangeStart.collectAsState()
    val rangeEnd by viewModel.rangeEnd.collectAsState()

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            NotesFilterSheet(
                tags = tags,
                selectedTagId = viewModel.selectedTagId.collectAsState().value,
                onTagSelected = viewModel::onTagFilterSelected,
                timeFilter = timeFilter,
                onTimeFilterSelected = viewModel::onTimeFilterSelected,
                rangeStart = rangeStart,
                rangeEnd = rangeEnd,
                onCustomRangeSelected = viewModel::onCustomRangeSelected,
                onlyReminder = viewModel.onlyReminder.collectAsStateWithLifecycle().value,
                onOnlyReminderChange = viewModel::onOnlyReminderChange,
                onClose = { showFilterSheet = false }
            )
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


    ModalNavigationDrawer(
        drawerState = drawerState, drawerContent = {
            DrawerContent(currentAvatar = avatar, onAvatarSelected = { type ->
                viewModel.onAvatarSelected(type)
                scope.launch { drawerState.close() }
            }, currentLanguage = lang, onLanguageSelected = { newLang ->
                viewModel.onLanguageSelected(newLang)
                scope.launch { drawerState.close() }
            })
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
                    onSearchChange = viewModel::onSearchChange,
                    onGridToggleClicked = viewModel::onGridToggleClicked,
                    layoutMode = layoutMode,
                    onFilterClick = { showFilterSheet = true }
                )
            },
            bottomBar = {
                Column{
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = notes.size.toLocalizedDigits(locale).plus(" ").plus(stringResource(R.string.notes)),
                        textAlign = TextAlign.Center
                    )
                }
            },
            floatingActionButton = {
                CustomFab({ viewModel.onAddNoteClicked() })
            },
            floatingActionButtonPosition = FabPosition.End
        ) { inner ->

            when (layoutMode) {
                LayoutMode.LIST -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner)
                            .padding(horizontal = 6.dp),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        stickyHeader(key = "tags-header") {
                            TagsList(
                                labels = tags,
                                selectedTagId = viewModel.selectedTagId.collectAsState().value,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 8.dp),
                                cornerRadius = 18.dp,
                                horizontalGap = 18.dp,
                                verticalGap = 18.dp,
                                onLabelClick = viewModel::onTagFilterSelected
                            )
                        }

                        items(pinnedNote, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.onNoteDetailsClicked(note.id) },
                                pinNote = { viewModel.pinNote(note.id) },
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
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        items(others, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.onNoteDetailsClicked(note.id) },
                                pinNote = { viewModel.pinNote(note.id) },
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
                            .padding(horizontal = 6.dp),
                        state = gridState,
                        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item(
                            key = "tags-header", span = StaggeredGridItemSpan.FullLine
                        ) {
                            TagsList(
                                labels = tags,
                                selectedTagId = viewModel.selectedTagId.collectAsState().value,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 8.dp),
                                cornerRadius = 18.dp,
                                horizontalGap = 18.dp,
                                verticalGap = 18.dp,
                                onLabelClick = viewModel::onTagFilterSelected
                            )
                        }

                        items(pinnedNote, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.onNoteDetailsClicked(note.id) },
                                pinNote = { viewModel.pinNote(note.id) },
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
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        items(others, key = { it.id }) { note ->
                            CustomNoteCard(
                                note = note,
                                isSelected = note.id in selected,
                                onClick = { viewModel.onNoteDetailsClicked(note.id) },
                                pinNote = { viewModel.pinNote(note.id) },
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
