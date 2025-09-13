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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.core.enums.LayoutMode
import com.example.noteapp.domain.model.Note
import com.example.noteapp.presentation.screens.home.model.NotesHomeColors
import com.example.noteapp.presentation.screens.home.model.NotesHomeMetrics
import com.example.noteapp.presentation.screens.home.model.NotesHomeShapes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomNoteCard(
    note: Note,
    colors: NotesHomeColors,
    shapes: NotesHomeShapes,
    metrics: NotesHomeMetrics,
    titleStyle: TextStyle,
    bodyStyle: TextStyle,
    chipTextStyle: TextStyle,
    layoutMode: LayoutMode,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean,
) {

    val highlight =
        if (isSelected) MaterialTheme.colorScheme.primary else Transparent

    Surface(
        shape = shapes.cardShape,
        color = colors.cardContainer,
        contentColor = colors.cardContent,
        tonalElevation = metrics.cardElevation
    ) {
        Column(
            modifier = Modifier
                .border(2.dp, highlight, shapes.cardShape)
                .clip(shapes.cardShape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
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
                        Badges(
                            note.timeBadge,
                            note.categoryBadge,
                            colors,
                            shapes,
                            metrics,
                            chipTextStyle
                        )
                    }
                }

                LayoutMode.GRID -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badges(
                            note.timeBadge,
                            note.categoryBadge,
                            colors,
                            shapes,
                            metrics,
                            chipTextStyle
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Badges(
    timeBadge: String?, categoryBadge: String?, colors: NotesHomeColors,
    shapes: NotesHomeShapes,
    metrics: NotesHomeMetrics,
    chipTextStyle: TextStyle,
) {
    timeBadge?.let {
        CustomPillChip(
            text = it,
            containerColor = colors.chipContainerPrimary,
            contentColor = colors.chipContentPrimary,
            shape = shapes.chipShape,
            hPad = metrics.chipHorizontalPadding,
            vPad = metrics.chipVerticalPadding,
            textStyle = chipTextStyle,
            icon = ImageVector.vectorResource(R.drawable.ic_timer),
        )
    }
    categoryBadge?.let {
        CustomPillChip(
            text = it,
            containerColor = colors.chipContainerSecondary,
            contentColor = colors.chipContentSecondary,
            shape = shapes.chipShape,
            hPad = metrics.chipHorizontalPadding,
            vPad = metrics.chipVerticalPadding,
            textStyle = chipTextStyle,
            icon = null,
        )
    }
}