package com.app.noteapp.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
//    private val fontRepository: FontRepository
): ViewModel() {
//    val currentFont = fontRepository.currentFont
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000),
//            AppFont.PELAK
//        )
//
//    fun onFontSelected(font: AppFont) {
//        viewModelScope.launch {
//            fontRepository.setFont(font)
//        }
//    }
}