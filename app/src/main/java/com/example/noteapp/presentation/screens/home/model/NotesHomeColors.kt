package com.example.noteapp.presentation.screens.home.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import com.example.noteapp.presentation.theme.Background
import com.example.noteapp.presentation.theme.Primary
import com.example.noteapp.presentation.theme.SecondaryBg
import com.example.noteapp.presentation.theme.SecondaryContent

@Immutable
data class NotesHomeColors(
    val background: Color = Background,
    val topBarContainer: Color = Color.Transparent,
    val searchBackground: Color = Background,
    val searchContent: Color = com.example.noteapp.presentation.theme.Black,
    val listTitle: Color = DarkGray,
    val cardContainer: Color = com.example.noteapp.presentation.theme.White,
    val cardContent: Color = com.example.noteapp.presentation.theme.Black,
    val cardShadow: Color = Black,
    val chipContainerPrimary: Color = Primary,
    val chipContentPrimary: Color = com.example.noteapp.presentation.theme.White,
    val chipContainerSecondary: Color = SecondaryBg,
    val chipContentSecondary: Color = SecondaryContent,
    val bottomBarContainer: Color = Background,
    val bottomBarContent: Color = Black,
    val fabContainer: Color = Primary,
    val fabContent: Color = com.example.noteapp.presentation.theme.White,
)