package com.app.noteapp.data.repository

import com.app.noteapp.data.local.font.FontPreferences
import com.app.noteapp.domain.model.AppFont
import com.app.noteapp.domain.repository.FontRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FontRepositoryImpl @Inject constructor(
    private val prefs: FontPreferences
) : FontRepository {

    override val currentFont: Flow<AppFont> = prefs.currentFont

    override suspend fun setFont(font: AppFont) {
        prefs.setFont(font)
    }
}