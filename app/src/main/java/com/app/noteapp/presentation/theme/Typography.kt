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
        TextScalePref.XS -> 0.85f
        TextScalePref.S  -> 0.93f
        TextScalePref.M  -> 1.0f
        TextScalePref.L  -> 1.08f
        TextScalePref.XL -> 1.16f
    }

    val family: FontFamily = fontFamilyFor(font)

    fun sz(baseSp: Int) = (baseSp * scale).sp

    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(32),
            fontWeight = FontWeight.Bold
        ),
        displayMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(28),
            fontWeight = FontWeight.Bold
        ),
        displaySmall = TextStyle(
            fontFamily = family,
            fontSize = sz(26),
            fontWeight = FontWeight.Bold
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(32),
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(28),
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontSize = sz(26),
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(24),
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(22),
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontSize = sz(20),
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontSize = sz(18),
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontSize = sz(16),
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontSize = sz(14),
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