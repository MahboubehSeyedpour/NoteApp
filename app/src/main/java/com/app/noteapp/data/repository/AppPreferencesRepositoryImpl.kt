package com.app.noteapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref
import com.app.noteapp.domain.repository.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesRepositoryImpl  @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppPreferencesRepository {

    private object Keys {
        val LANGUAGE   = stringPreferencesKey("language")      // "fa" | "en"
        val FONT       = stringPreferencesKey("app_font")      // AppFont.key
        val AVATAR     = stringPreferencesKey("avatar_type")   // "FEMALE" | "MALE"
        val THEME_MODE = stringPreferencesKey("theme_mode")    // "SYSTEM" | "LIGHT" | "DARK"
        val TEXT_SCALE = stringPreferencesKey("text_scale")    // "XS"|"S"|"M"|"L"|"XL"
    }

    // ---- Language ----
    override val languageFlow: Flow<LanguagePref> =
        dataStore.data.map { prefs -> LanguagePref.fromPref(prefs[Keys.LANGUAGE]) }

    override suspend fun setLanguage(lang: LanguagePref) {
        dataStore.edit { it[Keys.LANGUAGE] = lang.prefValue }
    }

    // ---- Font ----
    override val fontFlow: Flow<FontPref> =
        dataStore.data.map { prefs ->
            val lang = LanguagePref.fromPref(prefs[Keys.LANGUAGE])
            FontPref.fromKey(
                key = prefs[Keys.FONT],
                language = lang
            )
        }


    override suspend fun setFont(font: FontPref) {
        dataStore.edit { it[Keys.FONT] = font.key }
    }

    // ---- Avatar ----
    override val avatarFlow: Flow<AvatarPref> =
        dataStore.data.map { prefs ->
            runCatching { AvatarPref.valueOf(prefs[Keys.AVATAR] ?: AvatarPref.MALE.name) }
                .getOrDefault(AvatarPref.MALE)
        }

    override suspend fun setAvatar(type: AvatarPref) {
        dataStore.edit { it[Keys.AVATAR] = type.name }
    }

    // ---- ThemeMode ----
    override val themeModeFlow: Flow<ThemeModePref> =
        dataStore.data.map { prefs ->
            runCatching { ThemeModePref.valueOf(prefs[Keys.THEME_MODE] ?: ThemeModePref.SYSTEM.name) }
                .getOrDefault(ThemeModePref.SYSTEM)
        }

    override suspend fun setThemeMode(mode: ThemeModePref) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    // ---- TextScale ----
    override val textScaleFlow: Flow<TextScalePref> =
        dataStore.data.map { prefs ->
            runCatching { TextScalePref.valueOf(prefs[Keys.TEXT_SCALE] ?: TextScalePref.M.name) }
                .getOrDefault(TextScalePref.M)
        }

    override suspend fun setTextScale(scale: TextScalePref) {
        dataStore.edit { it[Keys.TEXT_SCALE] = scale.name }
    }
}