package com.app.noteapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.noteapp.domain.repository.FontRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreFontRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FontRepository {

    private object Keys {
        val FONT = stringPreferencesKey("app_font")
    }

//    override val currentFont: Flow<AppFont> =
//        dataStore.data.map { prefs ->
//            AppFont.fromKey(prefs[Keys.FONT])
//        }
//
//    override suspend fun setFont(font: AppFont) {
//        dataStore.edit { prefs ->
//            prefs[Keys.FONT] = font.key
//        }
//    }
}