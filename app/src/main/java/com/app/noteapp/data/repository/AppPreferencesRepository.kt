package com.app.noteapp.data.repository

import com.app.noteapp.domain.common_model.TextScale
import com.app.noteapp.domain.common_model.ThemeMode
import com.app.noteapp.domain.common_model.AppFont
import com.app.noteapp.domain.common_model.AppLanguage
import com.app.noteapp.domain.common_model.AvatarType
import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    val languageFlow: Flow<AppLanguage>
    suspend fun setLanguage(lang: AppLanguage)

    val fontFlow: Flow<AppFont>
    suspend fun setFont(font: AppFont)

    val avatarFlow: Flow<AvatarType>
    suspend fun setAvatar(type: AvatarType)

    val themeModeFlow: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)

    val textScaleFlow: Flow<TextScale>
    suspend fun setTextScale(scale: TextScale)
}