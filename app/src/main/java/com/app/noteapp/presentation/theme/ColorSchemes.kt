package com.app.noteapp.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LuxLightColorScheme = lightColorScheme(
    primary            = PrimaryLight,
    onPrimary          = Color.White,
    primaryContainer   = PrimaryLight.copy(alpha = 0.14f),
    onPrimaryContainer = PrimaryDark,

    secondary          = SecondaryLight,
    onSecondary        = Color.White,
    secondaryContainer = SecondaryLight.copy(alpha = 0.16f),
    onSecondaryContainer = SecondaryDark,

    tertiary           = TertiaryLight,
    onTertiary         = Color.White,
    tertiaryContainer  = TertiaryLight.copy(alpha = 0.16f),
    onTertiaryContainer = TertiaryDark,

    error              = Error,
    onError            = Color.White,

    background         = BackgroundLight,
    onBackground       = PrimaryLight,

    surface            = Surface,
    onSurface          = OnSurface,

    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = SurfaceVariant,

    outline            = Outline,
    outlineVariant     = OutlineVariant,

    surfaceTint        = PrimaryLight,
)

val LuxDarkColorScheme = darkColorScheme(
    primary            = PrimaryLight,
    onPrimary          = Color.White,
    primaryContainer   = PrimaryLight.copy(alpha = 0.14f),
    onPrimaryContainer = PrimaryDark,

    secondary          = SecondaryLight,
    onSecondary        = Color.White,
    secondaryContainer = SecondaryLight.copy(alpha = 0.16f),
    onSecondaryContainer = SecondaryDark,

    tertiary           = TertiaryLight,
    onTertiary         = Color.White,
    tertiaryContainer  = TertiaryLight.copy(alpha = 0.16f),
    onTertiaryContainer = TertiaryDark,

    error              = Error,
    onError            = Color.White,

    background         = BackgroundLight,
    onBackground       = PrimaryLight,

    surface            = Surface,
    onSurface          = OnSurface,

    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = SurfaceVariant,

    outline            = Outline,
    outlineVariant     = OutlineVariant,

    surfaceTint        = PrimaryLight,
)
