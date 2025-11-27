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
    primary            = PrimaryDark,
    onPrimary          = Color.White,
    primaryContainer   = PrimaryDark,
    onPrimaryContainer = Color.White,

    secondary          = SecondaryDark,
    onSecondary        = OnSecondaryLight,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color.White,

    tertiary           = TertiaryLight,
    onTertiary         = Color.White,
    tertiaryContainer  = TertiaryContainer,
    onTertiaryContainer = Color.White,

    error              = Error,
    onError            = Color.White,

    background         = BackgroundDark,
    onBackground       = Color.White,

    surface            = Surface,
    onSurface          = Color.White,

    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = OnSurfaceVariant,

    outline            = Outline,
    outlineVariant     = OutlineVariant,

    surfaceTint        = PrimaryLight,
)
