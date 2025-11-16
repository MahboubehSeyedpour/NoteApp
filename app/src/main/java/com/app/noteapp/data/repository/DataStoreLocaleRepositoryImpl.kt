package com.app.noteapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.noteapp.domain.model.AppLanguage
import com.app.noteapp.domain.repository.LocaleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreLocaleRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LocaleRepository {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
    }

    override val languageFlow: Flow<AppLanguage> = dataStore.data.map { prefs ->
        val stored = prefs[Keys.LANGUAGE]
        AppLanguage.fromPref(stored)   // DEFAULT = FA
    }

    override suspend fun setLanguage(lang: AppLanguage) {
        dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = lang.prefValue
        }
    }
}