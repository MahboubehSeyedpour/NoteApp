package com.app.noteapp.domain.usecase

import com.app.noteapp.domain.common_model.AppLanguage
import com.app.noteapp.domain.repository.LocaleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LanguageUseCase @Inject constructor(
    private val repo: LocaleRepository
) {
    operator fun invoke(): Flow<AppLanguage> = repo.languageFlow
    suspend operator fun invoke(lang: AppLanguage) = repo.setLanguage(lang)
}
