package com.app.noteapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeModePref { SYSTEM, LIGHT, DARK }
enum class TextScalePref { XS, S, M, L, XL }

interface UiPreferencesRepository {
    val themeModeFlow: Flow<ThemeModePref>
    val textScaleFlow: Flow<TextScalePref>
    suspend fun setThemeMode(mode: ThemeModePref)
    suspend fun setTextScale(scale: TextScalePref)
}

@Singleton
class DataStoreUiPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UiPreferencesRepository {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val TEXT_SCALE = stringPreferencesKey("text_scale")
    }

    override val themeModeFlow: Flow<ThemeModePref> =
        dataStore.data.map { prefs ->
            when (prefs[Keys.THEME_MODE]) {
                "LIGHT" -> ThemeModePref.LIGHT
                "DARK" -> ThemeModePref.DARK
                else -> ThemeModePref.SYSTEM
            }
        }

    override val textScaleFlow: Flow<TextScalePref> =
        dataStore.data.map { prefs ->
            when (prefs[Keys.TEXT_SCALE]) {
                "XS" -> TextScalePref.XS
                "S"  -> TextScalePref.S
                "L"  -> TextScalePref.L
                "XL" -> TextScalePref.XL
                else -> TextScalePref.M
            }
        }

    override suspend fun setThemeMode(mode: ThemeModePref) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override suspend fun setTextScale(scale: TextScalePref) {
        dataStore.edit { it[Keys.TEXT_SCALE] = scale.name }
    }
}