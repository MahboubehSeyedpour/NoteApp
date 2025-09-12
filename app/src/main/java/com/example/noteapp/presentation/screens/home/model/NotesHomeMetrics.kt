package com.example.noteapp.presentation.screens.home.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class NotesHomeMetrics(
    val screenPadding: Dp = 16.dp,
    val cardPadding: Dp = 16.dp,
    val verticalSpacing: Dp = 12.dp,
    val iconSize: Dp = 24.dp,
    val chipHorizontalPadding: Dp = 10.dp,
    val chipVerticalPadding: Dp = 6.dp,
    val cardElevation: Dp = 1.dp
)