package com.app.noteapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.domain.usecase.AppPreferencesUseCase
import com.app.noteapp.presentation.screens.settings.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
//    private val fontRepository: FontRepository
    private val appPreferencesUseCase: AppPreferencesUseCase
): ViewModel() {
    val preferencesState: StateFlow<SettingsUiState> =
        appPreferencesUseCase()
            .map { s ->
                SettingsUiState(
                    isLoading = false,
                    themeMode = s.themeMode,
                    language = s.language,
                    font = s.fontPref,
                    avatar = s.avatar,
                    textScale = s.textScale
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState(isLoading = true)
            )
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