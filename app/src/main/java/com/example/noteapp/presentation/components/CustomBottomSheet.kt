package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.noteapp.presentation.screens.BottomSheetRowModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    rows: List<BottomSheetRowModel>,
) {

    val context = LocalContext.current

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
            rows.forEach {
                BottomSheetRow(
                    leadingIcon = ImageVector.vectorResource(it.icon),
                    title = context.getString(it.title),
                    trailing = it.value,
                    onClick = it.onRowClicked
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}