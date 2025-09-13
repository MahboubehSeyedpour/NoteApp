package com.example.noteapp.presentation.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.R
import com.example.noteapp.core.enums.LayoutMode
import com.example.noteapp.domain.model.Note
import com.example.noteapp.presentation.components.CustomBottomBar
import com.example.noteapp.presentation.components.CustomNoteCard
import com.example.noteapp.presentation.components.HomeTopBarConfig
import com.example.noteapp.presentation.components.NotesTopBar
import com.example.noteapp.presentation.navigation.Screens
import com.example.noteapp.presentation.theme.Background
import com.example.noteapp.presentation.theme.NoteAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val notes by viewModel.notes.collectAsState()
    val context = LocalContext.current
    val layoutMode by viewModel.layoutMode.collectAsState()
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }
    val query by viewModel.query.collectAsState()
    val selected by viewModel.selected.collectAsState()

    LaunchedEffect(viewModel.events) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is HomeEvents.NavigateToNoteDetailsScreen -> navController.navigate(
                        route = "${Screens.NoteDetailsScreen.route}?id=${event.noteId}"
                    )

                    HomeEvents.NavigateToAddNoteScreen -> navController.navigate(Screens.AddNoteScreen.route)
                    is HomeEvents.Error -> Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    if (confirmDeleteId != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text(context.getString(R.string.dialog_delete_title)) },
            text = { Text(context.getString(R.string.dialog_delete_text)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSelected()
                    confirmDeleteId = null
                }) { Text(context.getString(R.string.dialog_delete_confirm_btn)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) {
                    Text(context.getString(R.string.dialog_delete_dismiss_btn))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGray)
    ) {
        Content(
            topBarConfig = HomeTopBarConfig(
                avatar = painterResource(R.mipmap.user_avatar_foreground),
                searchText = query,
                onSearchChange = viewModel::onSearchChange,
                onGridToggle = { viewModel.onGridToggleClicked() },
                onMenuClick = { /* TODO */ },
                gridToggleIcon = when (layoutMode) {
                    LayoutMode.LIST -> ImageVector.vectorResource(R.drawable.ic_grid)
                    LayoutMode.GRID -> ImageVector.vectorResource(R.drawable.ic_list)
                },
                menuIcon = Icons.Outlined.Menu,
                placeholder = context.getString(R.string.search_placeholder),
            ),
            titleText = context.getString(R.string.note_list_header),
            notes = notes,
            layoutMode = layoutMode,
            selectedIds = selected,
            onNoteClick = { id ->
                if (selected.isEmpty()) viewModel.onNoteDetailsClicked(id)
                else viewModel.toggleSelection(id)
            },
            onDeleteSelected = { id ->
                confirmDeleteId = id
            },
            onPinSelected = { viewModel.pinSelected() },
            onLabelsClick = {},
            onFabClick = { viewModel.onAddNoteClicked() },
            bottomBarLabel = "Labels",
            onNoteLongPress = { id -> viewModel.toggleSelection(id) },
            onClearSelection = { viewModel.clearSelection() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    topBarConfig: HomeTopBarConfig,
    titleText: String,
    notes: List<Note>,
    selectedIds: Set<Long>,
    onDeleteSelected: (Long) -> Unit,
    onPinSelected: () -> Unit,
    onClearSelection: () -> Unit,
    layoutMode: LayoutMode,
    onNoteClick: (Long) -> Unit,
    onLabelsClick: () -> Unit,
    onFabClick: () -> Unit,
    onNoteLongPress: (Long) -> Unit,
    titleTextStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.SemiBold
    ),
    noteTitleStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.SemiBold
    ),
    noteBodyStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    ),
    chipTextStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Medium
    ),
    bottomBarLabel: String,
    fabIcon: ImageVector = Icons.Outlined.Add
) {

    val context = LocalContext.current
    val inSelection = selectedIds.isNotEmpty()

    Scaffold(
        containerColor = Background,
        topBar = {
            if (inSelection) {
                TopAppBar(
                    title = { Text("${selectedIds.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = onClearSelection) {
                            Icon(Icons.Outlined.Close, contentDescription = "Clear selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = onPinSelected) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.ic_pin),
                                modifier = Modifier.size(24.dp),
                                contentDescription = "Pin"
                            )
                        }
                        IconButton(onClick = { onDeleteSelected(selectedIds.first()) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                        }
                    }
                )
            } else {
                NotesTopBar(config = topBarConfig)
            }
        },
        bottomBar = {
            if (inSelection) {
                BottomAppBar {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onPinSelected) { Text(context.getString(R.string.pin)) }
                    TextButton(onClick = { onDeleteSelected(selectedIds.first()) }) {
                        Text(
                            context.getString(
                                R.string.delete
                            )
                        )
                    }
                }
            } else {
                CustomBottomBar(
                    label = bottomBarLabel,
                    onLabelsClick = onLabelsClick,
                    onFabClick = onFabClick,
                    fabIcon = fabIcon
                )
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = dimensionResource(R.dimen.screen_padding))
        ) {
            Spacer(Modifier.height(dimensionResource(R.dimen.screen_padding)))
            if (!inSelection) {
                Text(text = titleText, style = titleTextStyle, color = DarkGray)
                Spacer(Modifier.height(dimensionResource(R.dimen.screen_padding) * 2))
            }
            when (layoutMode) {
                LayoutMode.LIST -> NotesList(
                    notes = notes,
                    noteTitleStyle = noteTitleStyle,
                    noteBodyStyle = noteBodyStyle,
                    chipTextStyle = chipTextStyle,
                    layoutMode = layoutMode,
                    onNoteClick = onNoteClick,
                    onNoteLongPress = onNoteLongPress,
                    selectedIds = selectedIds
                )

                LayoutMode.GRID -> NotesGrid(
                    notes = notes,
                    noteTitleStyle = noteTitleStyle,
                    noteBodyStyle = noteBodyStyle,
                    chipTextStyle = chipTextStyle,
                    layoutMode = layoutMode,
                    onNoteClick = onNoteClick,
                    onNoteLongPress = onNoteLongPress,
                    selectedIds = selectedIds
                )
            }
        }
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    noteTitleStyle: TextStyle,
    noteBodyStyle: TextStyle,
    chipTextStyle: TextStyle,
    layoutMode: LayoutMode,
    onNoteClick: (Long) -> Unit,
    onNoteLongPress: (Long) -> Unit,
    selectedIds: Set<Long>
) {

    val pinned = notes.filter { it.pinned }
    val others = notes.filterNot { it.pinned }

    LazyColumn(
        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding))
    ) {

        items(pinned, key = { it.id }) { note ->
            CustomNoteCard(
                note = note,
                isSelected = note.id in selectedIds,
                titleStyle = noteTitleStyle,
                bodyStyle = noteBodyStyle,
                chipTextStyle = chipTextStyle,
                onClick = { onNoteClick(note.id) },
                onLongClick = { onNoteLongPress(note.id) },
                layoutMode = layoutMode,
            )
        }

        if (pinned.isNotEmpty() && others.isNotEmpty()) {
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.screen_padding)),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }

        items(others, key = { it.id }) { note ->
            CustomNoteCard(
                note = note,
                isSelected = note.id in selectedIds,
                onClick = { onNoteClick(note.id) },
                onLongClick = { onNoteLongPress(note.id) },
                titleStyle = noteTitleStyle,
                bodyStyle = noteBodyStyle,
                chipTextStyle = chipTextStyle,
                layoutMode = layoutMode,
            )
        }
    }
}

@Composable
private fun NotesGrid(
    notes: List<Note>,
    noteTitleStyle: TextStyle,
    noteBodyStyle: TextStyle,
    chipTextStyle: TextStyle,
    layoutMode: LayoutMode,
    onNoteClick: (Long) -> Unit,
    onNoteLongPress: (Long) -> Unit,
    selectedIds: Set<Long>
) {
    val minCell = 160.dp
    val pinned = notes.filter { it.pinned }
    val others = notes.filterNot { it.pinned }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding))
    ) {
        if (pinned.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding)),
                    contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.screen_padding))
                ) {
                    items(pinned, key = { it.id }) { note ->
                        CustomNoteCard(
                            note = note,
                            isSelected = note.id in selectedIds,
                            onClick = { onNoteClick(note.id) },
                            onLongClick = { onNoteLongPress(note.id) },
                            titleStyle = noteTitleStyle,
                            bodyStyle = noteBodyStyle,
                            chipTextStyle = chipTextStyle,
                            layoutMode = layoutMode,
                        )
                        Spacer(Modifier.height(dimensionResource(R.dimen.screen_padding)))
                    }
                }
            }
            if (others.isNotEmpty()) {
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.screen_padding)),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.screen_padding)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding)),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(others, key = { it.id }) { note ->
                    CustomNoteCard(
                        note = note,
                        isSelected = note.id in selectedIds,
                        onClick = { onNoteClick(note.id) },
                        onLongClick = { onNoteLongPress(note.id) },
                        titleStyle = noteTitleStyle,
                        bodyStyle = noteBodyStyle,
                        chipTextStyle = chipTextStyle,
                        layoutMode = layoutMode,
                    )
                    Spacer(Modifier.height(dimensionResource(R.dimen.screen_padding)))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoteAppTheme {
        HomeScreen(navController = rememberNavController())
    }
}