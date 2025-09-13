package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.core.enums.LayoutMode
import com.example.noteapp.domain.model.Note

@Composable
fun NotesList(
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

    Column(Modifier.fillMaxSize()) {

        pinned.forEach {
            CustomNoteCard(
                note = it,
                isSelected = it.id in selectedIds,
                titleStyle = noteTitleStyle,
                bodyStyle = noteBodyStyle,
                chipTextStyle = chipTextStyle,
                onClick = { onNoteClick(it.id) },
                onLongClick = { onNoteLongPress(it.id) },
                layoutMode = layoutMode,
            )
        }

        if (pinned.isNotEmpty() && others.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.screen_padding)),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }


        when (layoutMode) {
            LayoutMode.LIST -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding))
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
                    }
                }
            }

            LayoutMode.GRID -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding)),
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding)),
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
}