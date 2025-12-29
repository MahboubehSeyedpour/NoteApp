package com.app.noteapp.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.noteapp.domain.model.preferences_model.FontPref

fun typographyFor(appFont: FontPref): Typography {
    val family: FontFamily = fontFamilyFor(appFont)

    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        ),
        displayMedium = TextStyle(
            fontFamily = family,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        ),
        displaySmall = TextStyle(
            fontFamily = family,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontSize = 32.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontSize = 28.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontSize = 26.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontSize = 24.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontSize = 22.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontSize = 20.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontSize = 18.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontSize = 16.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontSize = 14.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontSize = 12.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontSize = 11.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontSize = 10.sp,
        ),
    )
}