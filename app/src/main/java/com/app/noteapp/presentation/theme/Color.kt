//package com.app.noteapp.presentation.theme
//
//import androidx.compose.ui.graphics.Color
//
//val Purple80 = Color(0xFFD0BCFF)
//val PurpleGrey80 = Color(0xFFCCC2DC)
//val Pink80 = Color(0xFFEFB8C8)
//
//val Purple40 = Color(0xFF6650a4)
//val PurpleGrey40 = Color(0xFF625b71)
//val Pink40 = Color(0xFF7D5260)
//
//val Primary = Color(0xFF3384F9)
//val Background = Color(0xFFEFF0F4)
//val LightGray = Color(0xFFD4D4D5)
//val White = Color(0xFFFFFFFF)
//val SecondaryBg = Color(0x403384F9)
//val SecondaryContent = Color(0xFF3384F9)
//val Black = Color(0x80000000)

package com.app.noteapp.presentation.theme

import androidx.compose.ui.graphics.Color

// Primary
val LuxEmerald     = Color(0xFF5C8374) // primary
val LuxEmeraldDark = Color(0xFF1B4242) // primary dark

// Light surfaces
val LuxIvory       = Color(0xFFEFF0F4) // surface light
val LuxBone        = Color(0xFFA9D3C4) // slight tweak of 9EC8B9 (optional); or reuse 9EC8B9

// Dark surfaces
val LuxCharcoal    = Color(0xFF092635) // background dark
val LuxGraphite    = Color(0xFF12353A) // between 092635 and 1B4242; or reuse 1B4242

// Accents (reuse remaining tones)
val LuxGold        = Color(0xFF1B4242)
val LuxGoldDark    = Color(0xFF0F2E2E)
val LuxAmethyst    = Color(0xFF9EC8B9)
val LuxAmethystDark= Color(0xFF5C8374)

// Error (keep)
val LuxError       = Color(0xFFBA1A1A)
val LuxErrorDark   = Color(0xFFFFB4AB)

// Neutral ramps (optional)
val Neutral90 = Color(0xFFE7F3EF)
val Neutral98 = Color(0xFFF3FBF8)
val Neutral10 = Color(0xFF0B1616)
val Neutral20 = Color(0xFF152525)

// Used by mappers/VM
val ReminderTagColor = LuxEmerald