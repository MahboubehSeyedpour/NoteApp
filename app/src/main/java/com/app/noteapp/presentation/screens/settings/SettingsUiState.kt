package com.app.noteapp.presentation.screens.settings

import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref

data class SettingsUiState(
    val isLoading: Boolean = true,
    val language: LanguagePref = LanguagePref.FA,
    val font: FontPref = FontPref.SHABNAM,
    val themeMode: ThemeModePref = ThemeModePref.SYSTEM,
    val textScale: TextScalePref = TextScalePref.M,
    val avatar: AvatarPref = AvatarPref.MALE
)