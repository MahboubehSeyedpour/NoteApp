package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.noteapp.R

@Composable
fun NoteDetailScreenTopBar(
    onBack: () -> Unit,
    onNotificationClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularIconButton(
            onClick = onBack,
            icon = { Icon(ImageVector.vectorResource(R.drawable.ic_arrow_left), contentDescription = "Back") }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularIconButton(
                onClick = onNotificationClick,
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_notif),
                        contentDescription = "Notify"
                    )
                },
            )
            CircularIconButton(
                onClick = onShareClick,
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_send),
                        contentDescription = "Download"
                    )
                }
            )
        }
    }
}