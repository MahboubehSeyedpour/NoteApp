package com.app.noteapp.presentation.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val tagDefaultBG: Color,
    val tagDefaultFG: Color,
    val tagInfoBG: Color,
    val tagInfoFG: Color,
    val tagWarningBG: Color,
    val tagWarningFG: Color,
    val tagDangerBG: Color,
    val tagDangerFG: Color,
)

val LocalExtendedColors = compositionLocalOf {
    ExtendedColors(
        tagDefaultBG = Color(0x14000000),
        tagDefaultFG = Color(0xFF000000),
        tagInfoBG    = Color(0x14000000),
        tagInfoFG    = Color(0xFF000000),
        tagWarningBG = Color(0x14000000),
        tagWarningFG = Color(0xFF000000),
        tagDangerBG  = Color(0x14BA1A1A),
        tagDangerFG  = LuxError
    )
}

val ExtendedLight = ExtendedColors(
    tagDefaultBG = LuxEmerald.copy(alpha = 0.06f),
    tagDefaultFG = LuxEmerald,
    tagInfoBG    = LuxAmethyst.copy(alpha = 0.06f),
    tagInfoFG    = LuxAmethyst,
    tagWarningBG = LuxGold.copy(alpha = 0.10f),
    tagWarningFG = LuxGoldDark,
    tagDangerBG  = LuxError.copy(alpha = 0.08f),
    tagDangerFG  = LuxError,
)

val ExtendedDark = ExtendedColors(
    tagDefaultBG = LuxEmerald.copy(alpha = 0.20f),
    tagDefaultFG = Color(0xFFE0FFFA),
    tagInfoBG    = LuxAmethyst.copy(alpha = 0.20f),
    tagInfoFG    = Color(0xFFECE6FF),
    tagWarningBG = LuxGold.copy(alpha = 0.20f),
    tagWarningFG = Color(0xFFFFF0CC),
    tagDangerBG  = LuxErrorDark.copy(alpha = 0.22f),
    tagDangerFG  = Color(0xFF2B0B0B),
)
