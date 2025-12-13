package com.app.noteapp.domain.repository

import com.app.noteapp.domain.common_model.AvatarType
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val avatarFlow: Flow<AvatarType>
    suspend fun setAvatar(type: AvatarType)
}