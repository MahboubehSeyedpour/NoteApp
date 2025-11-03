package com.example.noteapp.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class Tag(
    val name: String,
    val color: Color
)
