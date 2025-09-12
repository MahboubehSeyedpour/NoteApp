package com.example.noteapp.presentation.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.noteapp.R
import com.example.noteapp.presentation.components.CustomBottomBar
import com.example.noteapp.presentation.components.CustomNoteCard
import com.example.noteapp.presentation.components.NotesTopBar
import com.example.noteapp.presentation.navigation.Screens
import com.example.noteapp.presentation.screens.home.model.HomeTopBarConfig
import com.example.noteapp.presentation.screens.home.model.NoteUi
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

    LaunchedEffect(viewModel.events) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is HomeEvents.NavigateToMovieDetailsScreen -> navController.navigate(
                        route = "${Screens.NoteDetailsScreen.route}?interScreenData=${event.interScreenData}"
                    )

                    HomeEvents.NavigateToSearchScreen -> navController.navigate(Screens.SearchScreen.route)
                    is HomeEvents.Error -> Toast.makeText(
                        context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
                onGridToggle = { /* TODO */ },
                onMenuClick = { /* TODO */ },
                gridToggleIcon = ImageVector.vectorResource(R.drawable.ic_grid),
                menuIcon = Icons.Outlined.Menu,
                placeholder = "Search your notes"
            ),
            titleText = "Recent All Note",
            notes = notes,
            onNoteClick = {},
            onLabelsClick = {},
            onFabClick = {},
            bottomBarLabel = "Labels",
        )
    }
}

@Composable
fun Notes(
    topBarConfig: HomeTopBarConfig,
    titleText: String,
    notes: List<NoteUi>,
    onNoteClick: (NoteUi) -> Unit,
    onLabelsClick: () -> Unit,
    onFabClick: () -> Unit,
    colors: NotesHomeColors = NotesHomeColors(),
    shapes: NotesHomeShapes = NotesHomeShapes(),
    metrics: NotesHomeMetrics = NotesHomeMetrics(),
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

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = metrics.screenPadding),
                verticalArrangement = Arrangement.spacedBy(metrics.verticalSpacing)
            ) {
                items(notes) { note ->
                    CustomNoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        colors = colors,
                        shapes = shapes,
                        metrics = metrics,
                        titleStyle = noteTitleStyle,
                        bodyStyle = noteBodyStyle,
                        chipTextStyle = chipTextStyle
                    )
                    Spacer(Modifier.height(metrics.verticalSpacing))
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