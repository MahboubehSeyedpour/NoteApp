package com.example.noteapp.presentation.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.noteapp.presentation.components.NotesTopBar
import com.example.noteapp.presentation.navigation.Screens
import com.example.noteapp.presentation.screens.home.model.HomeTopBarConfig
import com.example.noteapp.presentation.screens.home.model.NotesHomeColors
import com.example.noteapp.presentation.screens.home.model.NotesHomeMetrics
import com.example.noteapp.presentation.screens.home.model.NotesHomeShapes
import com.example.noteapp.presentation.theme.NoteAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val notes by viewModel.notes.collectAsState()
    val context = LocalContext.current
    val layoutMode by viewModel.layoutMode.collectAsState()
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }

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
            title = { Text("Delete note?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNoteById(confirmDeleteId!!)
                    confirmDeleteId = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGray)
    ) {
        Notes(
            topBarConfig = HomeTopBarConfig(
                avatar = painterResource(R.mipmap.user_avatar_foreground),
                searchText = "",
                onSearchChange = {},
                onGridToggle = {  viewModel.onGridToggleClicked() },
                onMenuClick = { /* TODO */ },
                gridToggleIcon = when (layoutMode) {
                    LayoutMode.LIST -> ImageVector.vectorResource(R.drawable.ic_grid)
                    LayoutMode.GRID -> ImageVector.vectorResource(R.drawable.ic_list)
                },
                menuIcon = Icons.Outlined.Menu,
                placeholder = "Search your notes"
            ),
            titleText = "Recent All Note",
            notes = notes,
            layoutMode = layoutMode,
            onNoteClick = { noteId -> viewModel.onNoteDetailsClicked(noteId) },
            onLabelsClick = {},
            onFabClick = { viewModel.onAddNoteClicked() },
            bottomBarLabel = "Labels",
            onNoteLongPress = { noteId -> confirmDeleteId = noteId },
        )
    }
}

@Composable
fun Notes(
    topBarConfig: HomeTopBarConfig,
    titleText: String,
    notes: List<Note>,
    layoutMode: LayoutMode,
    onNoteClick: (Long) -> Unit,
    onLabelsClick: () -> Unit,
    onFabClick: () -> Unit,
    colors: NotesHomeColors = NotesHomeColors(),
    shapes: NotesHomeShapes = NotesHomeShapes(),
    metrics: NotesHomeMetrics = NotesHomeMetrics(),
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
    Scaffold(
        containerColor = colors.background,
        topBar = {
            NotesTopBar(
                config = topBarConfig,
                colors = colors,
            )
        },
        bottomBar = {
            CustomBottomBar(
                label = bottomBarLabel,
                onLabelsClick = onLabelsClick,
                colors = colors,
                metrics = metrics,
                onFabClick = onFabClick,
                fabIcon = fabIcon
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = metrics.screenPadding)
        ) {
            Spacer(Modifier.height(metrics.verticalSpacing))
            Text(
                text = titleText,
                style = titleTextStyle,
                color = colors.listTitle
            )

            Spacer(Modifier.height(metrics.verticalSpacing))
            Spacer(Modifier.height(metrics.verticalSpacing))

            when (layoutMode) {
                LayoutMode.LIST -> NotesList(
                    notes = notes,
                    metrics = metrics,
                    colors = colors,
                    shapes = shapes,
                    noteTitleStyle = noteTitleStyle,
                    noteBodyStyle = noteBodyStyle,
                    chipTextStyle = chipTextStyle,
                    onNoteClick = onNoteClick,
                    layoutMode = layoutMode,
                    onNoteLongPress = onNoteLongPress
                )
                LayoutMode.GRID -> NotesGrid(
                    notes = notes,
                    metrics = metrics,
                    colors = colors,
                    shapes = shapes,
                    noteTitleStyle = noteTitleStyle,
                    noteBodyStyle = noteBodyStyle,
                    chipTextStyle = chipTextStyle,
                    onNoteClick = onNoteClick,
                    layoutMode = layoutMode,
                    onNoteLongPress = onNoteLongPress
                )
            }
        }
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    metrics: NotesHomeMetrics,
    colors: NotesHomeColors,
    shapes: NotesHomeShapes,
    noteTitleStyle: TextStyle,
    noteBodyStyle: TextStyle,
    chipTextStyle: TextStyle,
    onNoteClick: (Long) -> Unit,
    layoutMode: LayoutMode,
    onNoteLongPress: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = metrics.screenPadding),
        verticalArrangement = Arrangement.spacedBy(metrics.verticalSpacing)
    ) {
        items(notes, key = { it.id }) { note ->
            CustomNoteCard(
                note = note,
                colors = colors,
                shapes = shapes,
                metrics = metrics,
                titleStyle = noteTitleStyle,
                bodyStyle = noteBodyStyle,
                chipTextStyle = chipTextStyle,
                onClick = { onNoteClick(note.id) },
                layoutMode = layoutMode,
                onLongClick = { onNoteLongPress(note.id) }
            )
        }
    }
}

@Composable
private fun NotesGrid(
    notes: List<Note>,
    metrics: NotesHomeMetrics,
    colors: NotesHomeColors,
    shapes: NotesHomeShapes,
    noteTitleStyle: TextStyle,
    noteBodyStyle: TextStyle,
    chipTextStyle: TextStyle,
    onNoteClick: (Long) -> Unit,
    layoutMode: LayoutMode,
    onNoteLongPress: (Long) -> Unit
) {
    val minCell = 160.dp

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = minCell),
        contentPadding = PaddingValues(horizontal = metrics.screenPadding),
        verticalArrangement = Arrangement.spacedBy(metrics.verticalSpacing),
        horizontalArrangement = Arrangement.spacedBy(metrics.verticalSpacing)
    ) {
        items(notes, key = { it.id }) { note ->
            CustomNoteCard(
                note = note,
                onClick = { onNoteClick(note.id) },
                colors = colors,
                shapes = shapes,
                metrics = metrics,
                titleStyle = noteTitleStyle,
                bodyStyle = noteBodyStyle,
                chipTextStyle = chipTextStyle,
                layoutMode = layoutMode,
                onLongClick = { onNoteLongPress(note.id) }
            )
            Spacer(Modifier.height(metrics.verticalSpacing))
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