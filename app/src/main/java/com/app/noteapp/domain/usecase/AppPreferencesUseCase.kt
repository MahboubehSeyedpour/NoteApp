package com.app.noteapp.domain.usecase

import com.app.noteapp.domain.model.preferences_model.AppPreferences
import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref
import com.app.noteapp.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AppPreferencesUseCase @Inject constructor(
    private val repo: AppPreferencesRepository
) {
    operator fun invoke(): Flow<AppPreferences> =
        combine(
            repo.languageFlow,
            repo.fontFlow,
            repo.avatarFlow,
            repo.themeModeFlow,
            repo.textScaleFlow
        ) { lang, font, avatar, theme, scale ->
            AppPreferences(
                language = lang,
                fontPref = font,
                avatar = avatar,
                themeModePref = theme,
                textScalePref = scale
            )
        }

    suspend operator fun invoke(avatar: AvatarPref) {
        repo.setAvatar(avatar)
    }

    suspend operator fun invoke(lang: LanguagePref) {
        repo.setLanguage(lang)
    }

    suspend operator fun invoke(font: FontPref) {
        repo.setFont(font)
    }

    suspend operator fun invoke(mode: ThemeModePref) {
        repo.setThemeMode(mode)
    }

    suspend operator fun invoke(scale: TextScalePref) {
        repo.setTextScale(scale)
    }

}