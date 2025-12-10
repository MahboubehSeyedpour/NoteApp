package com.app.noteapp.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.app.noteapp.domain.model.AppFont

fun typographyFor(appFont: AppFont): Typography {
    val family: FontFamily = fontFamilyFor(appFont)

    return Typography(
        bodyLarge = TextStyle(
            fontFamily = family,
            fontSize = 16.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontSize = 22.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontSize = 14.sp,
        ),
    )
}