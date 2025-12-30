package com.app.noteapp.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref

@Immutable
data class AppShapes(
    val card: Shape = RoundedCornerShape(12.dp),
    val chip: Shape = RoundedCornerShape(12.dp)
)

val LocalAppShapes = staticCompositionLocalOf { AppShapes() }


object AppTheme {
    val extended: ExtendedColors
        @Composable get() = LocalExtendedColors.current

    val shapes: AppShapes
        @Composable get() = LocalAppShapes.current
}

@Composable
fun NoteAppTheme(
    themeMode: ThemeModePref,
    font: FontPref,
    textScale: TextScalePref,
    shapes: AppShapes = AppShapes(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        ThemeModePref.SYSTEM -> systemDark
        ThemeModePref.LIGHT  -> false
        ThemeModePref.DARK   -> true
    }

    val colors = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) LuxDarkColorScheme else LuxLightColorScheme
    }

    val extended = if (darkTheme) ExtendedDark else ExtendedLight

    val typography = typographyFor(font, textScale)

    CompositionLocalProvider(
        LocalExtendedColors provides extended,
        LocalAppShapes provides shapes
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = typography,
            content = content
        )
    }
}