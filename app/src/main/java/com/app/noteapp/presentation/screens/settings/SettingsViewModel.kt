package com.app.noteapp.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref
import com.app.noteapp.domain.usecase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCase: AppPreferencesUseCase,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val _events = MutableSharedFlow<SettingsEvents>()
    val events = _events.asSharedFlow()
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            useCase.invoke().collectLatest { s ->
                _uiState.value = SettingsUiState(
                    isLoading = false,
                    themeMode = s.themeModePref,
                    language = s.language,
                    font = s.fontPref
                )
            }
        }
    }

    fun onLanguageSelected(lang: LanguagePref) {
        viewModelScope.launch(io) {
            useCase.invoke(lang)
        }
    }

    fun onFontSelected(font: FontPref) {
        viewModelScope.launch(io) {
            useCase.invoke(font)
        }
    }

    fun onThemeModeSelected(mode: ThemeModePref) {
        viewModelScope.launch(io) {
            useCase.invoke(mode)
        }
    }

    fun onTextScaleSelected(scale: TextScalePref) {
        viewModelScope.launch(io) {
            useCase.invoke(scale)
        }
    }
}