package com.example.noteapp.presentation.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.R
import com.example.noteapp.core.enums.LayoutMode
import com.example.noteapp.presentation.components.CustomSearchField
import com.example.noteapp.presentation.components.HomeTopBarConfig
import com.example.noteapp.presentation.components.NotesList
import com.example.noteapp.presentation.components.NotesTopBar
import com.example.noteapp.presentation.navigation.Screens
import com.example.noteapp.presentation.theme.Background
import com.example.noteapp.presentation.theme.Primary
import com.example.noteapp.presentation.theme.White
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val notes by viewModel.notes.collectAsState()
    val context = LocalContext.current
    val layoutMode by viewModel.layoutMode.collectAsState()
    var confirmDeleteId by remember { mutableStateOf<Long?>(null) }
    val query by viewModel.query.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val inSelection = selected.isNotEmpty()

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
                    Text(context.getString(R.string.dialog_dismiss_btn))
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .background(Background)
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
        containerColor = Background,
        topBar = {
            if (inSelection) {
                TopAppBar(
                    title = { Text("${selected.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Clear selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.pinSelected() }) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.ic_pin),
                                modifier = Modifier.size(24.dp),
                                contentDescription = "Pin"
                            )
                        }
                        IconButton(onClick = { confirmDeleteId = selected.first() }) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.ic_trash),
                                contentDescription = "Delete"
                            )
                        }
                    }
                )
            } else {
                NotesTopBar(
                    config = HomeTopBarConfig(
                        avatar = painterResource(R.mipmap.img_man_foreground),
                        searchText = query,
                        onSearchChange = viewModel::onSearchChange,
                        onGridToggle = { viewModel.onGridToggleClicked() },
                        onMenuClick = { /* TODO */ },
                        gridToggleIcon = when (layoutMode) {
                            LayoutMode.LIST -> ImageVector.vectorResource(R.drawable.ic_vertical_list)
                            LayoutMode.GRID -> ImageVector.vectorResource(R.drawable.ic_grid_list)
                        },
                        menuIcon = ImageVector.vectorResource(R.drawable.ic_menu),
                        placeholder = context.getString(R.string.search_placeholder),
                    )
                )
            }
        },
        bottomBar = {
            if (inSelection) {
                BottomAppBar {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { viewModel.pinSelected() }) { Text(context.getString(R.string.pin)) }
                    TextButton(onClick = { confirmDeleteId = selected.first() }) {
                        Text(
                            context.getString(
                                R.string.delete
                            )
                        )
                    }
                }
            } else {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.screen_padding)),
                    colors = ButtonColors(
                        containerColor = Primary,
                        contentColor = White,
                        disabledContainerColor = Primary,
                        disabledContentColor = White
                    ),
                    onClick = { viewModel.onAddNoteClicked() }) {
                    Text(text = stringResource(R.string.note_add))
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
        ) {
            Spacer(Modifier.height(dimensionResource(R.dimen.screen_padding)))

            CustomSearchField(
                value = "",
                modifier = Modifier.fillMaxWidth(),
                contentColor = Black,
                containerColor = White,
                onValueChange = {},
                placeholder = "Search note",
                shape = RectangleShape
            )

            if (!inSelection) {
                Text(
                    text = context.getString(R.string.note_list_header),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = DarkGray
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.screen_padding) * 2))
            }

            NotesList(
                notes = notes,
                noteTitleStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Black
                ),
                noteBodyStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = com.example.noteapp.presentation.theme.Black,
                    fontSize = 12.sp,
                ),
                chipTextStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                layoutMode = layoutMode,
                onNoteClick = { id ->
                    if (selected.isEmpty()) viewModel.onNoteDetailsClicked(id)
                    else viewModel.toggleSelection(id)
                },
                onNoteLongPress = { id -> viewModel.toggleSelection(id) },
                selectedIds = selected
            )
        }
    }
}
