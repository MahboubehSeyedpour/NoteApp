package com.app.noteapp.domain.usecase

import com.app.noteapp.data.repository.AppPreferencesRepository
import com.app.noteapp.domain.common_model.TextScale
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TextScaleUseCase @Inject constructor(
    private val repo: AppPreferencesRepository
) {
    operator fun invoke(): Flow<TextScale> = repo.textScaleFlow
    suspend operator fun invoke(scale: TextScale) = repo.setTextScale(scale)
}
