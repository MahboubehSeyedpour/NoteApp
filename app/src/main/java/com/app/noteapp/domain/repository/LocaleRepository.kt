package com.app.noteapp.domain.repository

import com.app.noteapp.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface LocaleRepository {
    val languageFlow: Flow<AppLanguage>
    suspend fun setLanguage(lang: AppLanguage)
}