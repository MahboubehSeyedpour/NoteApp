package com.example.noteapp.presentation.components

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class HomeTopBarConfig(
    val avatar: Painter? = null,
    val searchText: String,
    val onSearchChange: (String) -> Unit,
    val onGridToggle: () -> Unit,
    val onMenuClick: () -> Unit,
    val gridToggleIcon: ImageVector,
    val menuIcon: ImageVector,
    val placeholder: String
)
