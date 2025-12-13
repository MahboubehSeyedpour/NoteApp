package com.app.noteapp.domain.usecase

import com.app.noteapp.domain.common_model.AvatarType
import com.app.noteapp.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AvatarTypeUseCase @Inject constructor(
    private val repo: UserPreferencesRepository
) {
    operator fun invoke(): Flow<AvatarType> = repo.avatarFlow
    suspend operator fun invoke(type: AvatarType) = repo.setAvatar(type)
}