package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    onBellClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularIconButton(
            onClick = onBack,
            icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularIconButton(
                onClick = onBellClick,
                icon = { Icon(ImageVector.vectorResource(R.drawable.ic_notif), contentDescription = "Notify") }
            )
            CircularIconButton(
                onClick = onDownloadClick,
                icon = { Icon(ImageVector.vectorResource(R.drawable.ic_download), contentDescription = "Download") }
            )
        }
    }
}