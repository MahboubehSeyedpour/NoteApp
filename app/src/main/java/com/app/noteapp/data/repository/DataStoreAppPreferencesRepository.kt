package com.app.noteapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.noteapp.domain.common_model.AppFont
import com.app.noteapp.domain.common_model.AppLanguage
import com.app.noteapp.domain.common_model.AvatarType
import com.app.noteapp.domain.common_model.TextScale
import com.app.noteapp.domain.common_model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreAppPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppPreferencesRepository {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")         // "fa" | "en"
        val FONT = stringPreferencesKey("app_font")            // AppFont.key
        val AVATAR = stringPreferencesKey("avatar_type")       // "FEMALE" | "MALE"
        val THEME_MODE = stringPreferencesKey("theme_mode")    // "SYSTEM" | "LIGHT" | "DARK"
        val TEXT_SCALE = stringPreferencesKey("text_scale")    // "XS"|"S"|"M"|"L"|"XL"
    }

    override val languageFlow: Flow<AppLanguage> =
        dataStore.data.map { prefs -> AppLanguage.fromPref(prefs[Keys.LANGUAGE]) }

    override suspend fun setLanguage(lang: AppLanguage) {
        dataStore.edit { it[Keys.LANGUAGE] = lang.prefValue }
    }

    override val fontFlow: Flow<AppFont> =
        dataStore.data.map { prefs -> AppFont.fromKey(prefs[Keys.FONT]) }

    override suspend fun setFont(font: AppFont) {
        dataStore.edit { it[Keys.FONT] = font.key }
    }

    override val avatarFlow: Flow<AvatarType> =
        dataStore.data.map { prefs ->
            runCatching { AvatarType.valueOf(prefs[Keys.AVATAR] ?: AvatarType.MALE.name) }
                .getOrDefault(AvatarType.MALE)
        }

    override suspend fun setAvatar(type: AvatarType) {
        dataStore.edit { it[Keys.AVATAR] = type.name }
    }

    override val themeModeFlow: Flow<ThemeMode> =
        dataStore.data.map { prefs ->
            runCatching { ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name) }
                .getOrDefault(ThemeMode.SYSTEM)
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override val textScaleFlow: Flow<TextScale> =
        dataStore.data.map { prefs ->
            runCatching { TextScale.valueOf(prefs[Keys.TEXT_SCALE] ?: TextScale.M.name) }
                .getOrDefault(TextScale.M)
        }

    override suspend fun setTextScale(scale: TextScale) {
        dataStore.edit { it[Keys.TEXT_SCALE] = scale.name }
    }
}