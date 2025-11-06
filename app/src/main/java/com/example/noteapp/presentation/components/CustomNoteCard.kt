package com.example.noteapp.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.core.enums.LayoutMode
import com.example.noteapp.domain.model.Note
import com.example.noteapp.presentation.theme.Black
import com.example.noteapp.presentation.theme.LocalAppShapes
import com.example.noteapp.presentation.theme.White

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomNoteCard(
    note: Note,
    titleStyle: TextStyle,
    bodyStyle: TextStyle,
    chipTextStyle: TextStyle,
    layoutMode: LayoutMode,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean,
) {

    val highlight = if (isSelected) MaterialTheme.colorScheme.primary else Transparent

    val shapes = LocalAppShapes.current

    Surface(
        shape = shapes.card,
        color = White,
        contentColor = Black,
        tonalElevation = dimensionResource(R.dimen.card_elevation)
    ) {
        Column(
            modifier = Modifier
                .border(2.dp, highlight, shapes.card)
                .clip(shapes.card)
                .combinedClickable(
                    onClick = onClick, onLongClick = onLongClick
                )
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.card_padding))
                .testTag("note-${note.id}")
        ) {
            Text(
                text = note.title,
                style = titleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = note.description ?: "",
                style = bodyStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(12.dp))

            when (layoutMode) {
                LayoutMode.LIST -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TagFlowList(
                            labels = buildList {
                            note.tag?.let { add(it) }
                            note.reminderTag?.let { add(it) }
                        }, onLabelClick = {}, trailingIcon = null
                        )
                    }
                }

                LayoutMode.GRID -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TagFlowList(
                            labels = buildList {
                            note.tag?.let { add(it) }
                            note.reminderTag?.let { add(it) }
                        }, onLabelClick = {}, trailingIcon = null
                        )
                    }
                }
            }
        }
    }
}