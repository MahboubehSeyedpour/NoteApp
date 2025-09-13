package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,

    row1Icon: ImageVector,
    row1Title: String,
    row1Value: String,
    onRow1Click: () -> Unit,

    row2Icon: ImageVector,
    row2Title: String,
    row2Value: String,
    onRow2Click: () -> Unit,

    row3Icon: ImageVector,
    row3Title: String,
    row3Value: String,
    onRow3Click: () -> Unit,

    row4Icon: ImageVector,
    row4Title: String,
    onRow4PlusClick: () -> Unit
) {
    if (!visible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BottomSheetRow (
                leadingIcon = row1Icon,
                title = row1Title,
                trailing = { TrailingText(value = row1Value) },
                onClick = onRow1Click
            )
            BottomSheetRow(
                leadingIcon = row2Icon,
                title = row2Title,
                trailing = { TrailingText(value = row2Value) },
                onClick = onRow2Click
            )
            BottomSheetRow(
                leadingIcon = row3Icon,
                title = row3Title,
                trailing = { TrailingText(value = row3Value) },
                onClick = onRow3Click
            )
            BottomSheetRow(
                leadingIcon = row4Icon,
                title = row4Title,
                trailing = {
                    IconButton(onClick = onRow4PlusClick) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add")
                    }
                },
                onClick = onRow4PlusClick
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TrailingText(value: String) {
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}