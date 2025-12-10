package com.app.noteapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.domain.model.AppFont
import com.app.noteapp.domain.repository.FontRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fontRepository: FontRepository
): ViewModel() {
    val currentFont = fontRepository.currentFont
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppFont.PELAK
        )

    fun onFontSelected(font: AppFont) {
        viewModelScope.launch {
            fontRepository.setFont(font)
        }
    }
}