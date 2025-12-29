package com.app.noteapp.domain.model.preferences_model


data class AppPreferences(
    val language: LanguagePref = LanguagePref.FA,
    val fontPref: FontPref = FontPref.SHABNAM,
    val avatar: AvatarPref = AvatarPref.MALE,
    val themeModePref: ThemeModePref = ThemeModePref.SYSTEM,
    val textScalePref: TextScalePref = TextScalePref.M,
)