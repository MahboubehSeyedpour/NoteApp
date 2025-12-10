package com.app.noteapp.data.local.font

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.noteapp.domain.model.AppFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FontPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val KEY_FONT = stringPreferencesKey("app_font")

    val currentFont: Flow<AppFont> = dataStore.data.map { prefs ->
            val stored = prefs[KEY_FONT]
            AppFont.fromKey(stored)
        }

    suspend fun setFont(font: AppFont) {
        dataStore.edit { prefs ->
            prefs[KEY_FONT] = font.key
        }
    }
}