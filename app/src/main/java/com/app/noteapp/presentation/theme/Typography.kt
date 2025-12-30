package com.app.noteapp.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.TextScalePref

fun typographyFor(font: FontPref, textScale: TextScalePref): Typography {
    val scale = when (textScale) {
        TextScalePref.XS -> 0.8f
        TextScalePref.S  -> 0.9f
        TextScalePref.M  -> 1.0f
        TextScalePref.L  -> 1.1f
        TextScalePref.XL -> 1.2f
    }

    val family: FontFamily = fontFamilyFor(font)

    fun sz(baseSp: Int) = (baseSp * scale).sp

    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(24),
            fontWeight = FontWeight.Bold
        ),
        displayMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(23),
            fontWeight = FontWeight.Bold
        ),
        displaySmall = TextStyle(
            fontFamily = family,
            fontSize = sz(22),
            fontWeight = FontWeight.Bold
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(21),
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(20),
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontSize = sz(19),
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(18),
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(17),
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontSize = sz(16),
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(15),
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(14),
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontSize = sz(13),
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(12),
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(11),
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontSize = sz(10),
        ),
    )
}