package com.app.noteapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.app.noteapp.domain.repository.UserPreferencesRepository
import com.app.noteapp.presentation.model.AvatarType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreUserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object Keys {
        val AVATAR = stringPreferencesKey("avatar_type")
    }

    override val avatarFlow: Flow<AvatarType> = dataStore.data.map { prefs ->
        when (prefs[Keys.AVATAR]) {
            "FEMALE" -> AvatarType.FEMALE
            "MALE", null -> AvatarType.MALE
            else -> AvatarType.MALE
        }
    }

    override suspend fun setAvatar(type: AvatarType) {
        dataStore.edit { it[Keys.AVATAR] = type.name }
    }
}