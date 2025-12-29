package com.app.noteapp.domain.repository

import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {

    val languageFlow: Flow<LanguagePref>
    suspend fun setLanguage(lang: LanguagePref)

    val fontFlow: Flow<FontPref>
    suspend fun setFont(font: FontPref)

    val avatarFlow: Flow<AvatarPref>
    suspend fun setAvatar(type: AvatarPref)

    val themeModeFlow: Flow<ThemeModePref>
    suspend fun setThemeMode(mode: ThemeModePref)

    val textScaleFlow: Flow<TextScalePref>
    suspend fun setTextScale(scale: TextScalePref)
}