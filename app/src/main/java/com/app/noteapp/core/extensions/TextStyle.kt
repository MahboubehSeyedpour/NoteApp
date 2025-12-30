package com.app.noteapp.core.extensions


import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

fun TextStyle.scaled(
    family: FontFamily,
    scale: Float
): TextStyle = copy(
    fontFamily = family,
    fontSize = fontSize * scale,
    lineHeight = lineHeight * scale
)