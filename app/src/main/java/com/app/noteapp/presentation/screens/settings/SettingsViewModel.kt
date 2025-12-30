package com.app.noteapp.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.noteapp.di.IoDispatcher
import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref
import com.app.noteapp.domain.usecase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val useCase: AppPreferencesUseCase,
    @IoDispatcher private val io: CoroutineDispatcher
) : ViewModel() {

    private val _events = MutableSharedFlow<SettingsEvents>()
    val events = _events.asSharedFlow()
    val uiState: StateFlow<SettingsUiState> =
        useCase()
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
                initialValue = SettingsUiState()
            )

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

    fun onAvatarSelected(avatar: AvatarPref) {
        viewModelScope.launch(io) {
            useCase.invoke(avatar)
        }
    }
}