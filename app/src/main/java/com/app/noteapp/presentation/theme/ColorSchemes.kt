package com.app.noteapp.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LuxLightColorScheme = lightColorScheme(
    primary            = LuxEmerald,
    onPrimary          = Color.White,
    primaryContainer   = LuxEmerald.copy(alpha = 0.14f),
    onPrimaryContainer = LuxEmeraldDark,

    secondary          = LuxGold,
    onSecondary        = Color.White,
    secondaryContainer = LuxGold.copy(alpha = 0.16f),
    onSecondaryContainer = LuxGoldDark,

    tertiary           = LuxAmethyst,
    onTertiary         = Color.White,
    tertiaryContainer  = LuxAmethyst.copy(alpha = 0.16f),
    onTertiaryContainer = LuxAmethystDark,

    error              = LuxError,
    onError            = Color.White,

    background         = LuxIvory,
    onBackground       = LuxCharcoal,

    surface            = Color.White,
    onSurface          = LuxCharcoal,

    surfaceVariant     = Neutral90,
    onSurfaceVariant   = LuxGraphite,

    outline            = LuxBone,
    outlineVariant     = Neutral90,

    // optional but nice to set
    surfaceTint        = LuxEmerald,
)

val LuxDarkColorScheme = darkColorScheme(
    primary            = LuxEmerald,
    onPrimary          = Color.White,
    primaryContainer   = LuxEmeraldDark,
    onPrimaryContainer = Color.White,

    secondary          = LuxGold,
    onSecondary        = LuxCharcoal,
    secondaryContainer = LuxGoldDark,
    onSecondaryContainer = Color.White,

    tertiary           = LuxAmethyst,
    onTertiary         = Color.White,
    tertiaryContainer  = LuxAmethystDark,
    onTertiaryContainer = Color.White,

    error              = LuxErrorDark,
    onError            = LuxCharcoal,

    background         = LuxCharcoal,
    onBackground       = Color.White,

    surface            = LuxGraphite,
    onSurface          = Color.White,

    surfaceVariant     = Neutral20,
    onSurfaceVariant   = Neutral98,

    outline            = Neutral20,
    outlineVariant     = Neutral10,

    surfaceTint        = LuxEmerald,
)
