package com.app.noteapp.domain.usecase

import com.app.noteapp.data.repository.AppPreferencesRepository
import com.app.noteapp.domain.common_model.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeModeUseCase @Inject constructor(
    private val repo: AppPreferencesRepository
) {
    operator fun invoke(): Flow<ThemeMode> = repo.themeModeFlow
    suspend operator fun invoke(mode: ThemeMode) = repo.setThemeMode(mode)
}