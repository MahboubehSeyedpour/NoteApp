package com.app.noteapp.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.domain.model.Note
import com.app.noteapp.presentation.theme.LocalAppShapes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomNoteCard(
    note: Note,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onMenuPin: () -> Unit,
    onMenuDelete: () -> Unit,
    noteTitleStyle: TextStyle,
    noteBodyStyle: TextStyle,
    chipTextStyle: TextStyle,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    style = noteTitleStyle
                )
                if (!note.description.isNullOrBlank()) {
                    Text(
                        text = note.description,
                        style = noteBodyStyle,
                        maxLines = 3
                    )
                }
            }


            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_options),
                        contentDescription = stringResource(R.string.more_options)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.pin)) },
                        onClick = {
                            menuExpanded = false
                            onMenuPin()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = {
                            menuExpanded = false
                            onMenuDelete()
                        }
                    )
                }
            }
        }
    }
}