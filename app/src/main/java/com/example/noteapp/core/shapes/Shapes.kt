package com.example.noteapp.core.shapes

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class Shapes(
    val cardShape: Shape = RoundedCornerShape(16.dp),
    val chipShape: Shape = RoundedCornerShape(16.dp)
)