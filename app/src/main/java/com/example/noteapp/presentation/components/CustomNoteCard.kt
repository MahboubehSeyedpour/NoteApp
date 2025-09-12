package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.presentation.screens.home.model.NoteUi
import com.example.noteapp.presentation.screens.home.model.NotesHomeColors
import com.example.noteapp.presentation.screens.home.model.NotesHomeMetrics
import com.example.noteapp.presentation.screens.home.model.NotesHomeShapes

@Composable
fun CustomNoteCard(
    note: NoteUi,
    onClick: () -> Unit,
    colors: NotesHomeColors,
    shapes: NotesHomeShapes,
    metrics: NotesHomeMetrics,
    titleStyle: TextStyle,
    bodyStyle: TextStyle,
    chipTextStyle: TextStyle
) {
    Surface(
        onClick = onClick,
        shape = shapes.cardShape,
        color = colors.cardContainer,
        contentColor = colors.cardContent,
        tonalElevation = metrics.cardElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(metrics.cardPadding)
        ) {
            Text(
                text = note.title,
                style = titleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = note.body,
                style = bodyStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                note.timeBadge?.let {
                    CustomPillChip(
                        text = it,
                        containerColor = colors.chipContainerPrimary,
                        contentColor = colors.chipContentPrimary,
                        shape = shapes.chipShape,
                        hPad = metrics.chipHorizontalPadding,
                        vPad = metrics.chipVerticalPadding,
                        textStyle = chipTextStyle,
                        icon = ImageVector.vectorResource(R.drawable.ic_timer)
                    )
                }
                note.categoryBadge?.let {
                    CustomPillChip(
                        text = it,
                        containerColor = colors.chipContainerSecondary,
                        contentColor = colors.chipContentSecondary,
                        shape = shapes.chipShape,
                        hPad = metrics.chipHorizontalPadding,
                        vPad = metrics.chipVerticalPadding,
                        textStyle = chipTextStyle,
                        icon = null
                    )
                }
            }
        }
    }
}