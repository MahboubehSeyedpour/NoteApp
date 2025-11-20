package com.app.noteapp.presentation.components

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
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.domain.model.Note

@Composable
fun NotesList(
    notes: List<Note>,
    noteTitleStyle: TextStyle,
    noteBodyStyle: TextStyle,
    chipTextStyle: TextStyle,
    layoutMode: LayoutMode,
    onNoteClick: (Long) -> Unit,
    onNoteLongPress: (Long) -> Unit,
    selectedIds: Set<Long>,
    onNoteMenuPin: (Long) -> Unit,
    onNoteMenuDelete: (Long) -> Unit,
) {
    val pinned = notes.filter { it.pinned }
    val others = notes.filterNot { it.pinned }

    Column(Modifier.fillMaxSize()) {

        pinned.forEach {
            CustomNoteCard(
                note = it,
                isSelected = it.pinned,
                onClick = { onNoteClick(it.id) },
                onLongPress = { onNoteLongPress(it.id) },
                onMenuPin = { onNoteMenuPin(it.id) },
                onMenuDelete = { onNoteMenuDelete(it.id) },
                noteTitleStyle = noteTitleStyle,
                noteBodyStyle = noteBodyStyle,
                chipTextStyle = chipTextStyle
            )
        }

        if (pinned.isNotEmpty() && others.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.screen_padding)),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
            )
        }


        when (layoutMode) {
            LayoutMode.LIST -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.screen_padding))
                ) {
                    items(notes) { note ->
                        val isSelected = note.id in selectedIds

                        CustomNoteCard(
                            note = note,
                            isSelected = isSelected,
                            onClick = { onNoteClick(note.id) },
                            onLongPress = { onNoteLongPress(note.id) },
                            onMenuPin = { onNoteMenuPin(note.id) },
                            onMenuDelete = { onNoteMenuDelete(note.id) },
                            noteTitleStyle = noteTitleStyle,
                            noteBodyStyle = noteBodyStyle,
                            chipTextStyle = chipTextStyle
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
                    items(notes) { note ->
                        val isSelected = note.id in selectedIds

                        CustomNoteCard(
                            note = note,
                            isSelected = isSelected,
                            onClick = { onNoteClick(note.id) },
                            onLongPress = { onNoteLongPress(note.id) },
                            onMenuPin = { onNoteMenuPin(note.id) },
                            onMenuDelete = { onNoteMenuDelete(note.id) },
                            noteTitleStyle = noteTitleStyle,
                            noteBodyStyle = noteBodyStyle,
                            chipTextStyle = chipTextStyle
                        )
                    }
                }
            }
        }
    }
}