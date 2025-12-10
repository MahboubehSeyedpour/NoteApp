package com.app.noteapp.domain.repository

import com.app.noteapp.domain.model.AppFont
import kotlinx.coroutines.flow.Flow

interface FontRepository {
    val currentFont: Flow<AppFont>
    suspend fun setFont(font: AppFont)
}