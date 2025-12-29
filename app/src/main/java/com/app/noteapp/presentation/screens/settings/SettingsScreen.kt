package com.app.noteapp.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.R
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    LaunchedEffect(viewModel.events) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is SettingsEvents.NavigateToHomeScreen -> navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings_general)) })
        }) { inner ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            SettingsContent(
                uiState = uiState,
                onLanguageSelected = viewModel::onLanguageSelected,
                onFontSelected = viewModel::onFontSelected,
                onThemeModeSelected = viewModel::onThemeModeSelected,
                onTextScaleSelected = viewModel::onTextScaleSelected,
                modifier = Modifier.padding(inner)
            )
        }
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onLanguageSelected: (LanguagePref) -> Unit,
    onFontSelected: (FontPref) -> Unit,
    onThemeModeSelected: (ThemeModePref) -> Unit,
    onTextScaleSelected: (TextScalePref) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_general),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Language
        item {
            SettingsSection(title = stringResource(R.string.choose_language)) {
                LanguageRow(
                    selected = uiState.language, onSelect = onLanguageSelected
                )
            }
        }

        // Font
        item {
            SettingsSection(title = stringResource(R.string.choose_font)) {
                FontRow(
                    selected = uiState.font, onSelect = onFontSelected
                )
            }
        }

        // Theme
        item {
            SettingsSection(title = stringResource(R.string.choose_theme)) {
                ThemeRow(
                    selected = uiState.themeMode, onSelect = onThemeModeSelected
                )
            }
        }

        // Text scale
        item {
            SettingsSection(title = stringResource(R.string.choose_text_scale)) {
                TextScaleRow(
                    selected = uiState.textScale, onSelect = onTextScaleSelected
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String, content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Surface(
            tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun LanguageRow(
    selected: LanguagePref, onSelect: (LanguagePref) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LanguageOptionRow(
            text = "فارسی",
            selected = selected == LanguagePref.FA,
        ) { onSelect(LanguagePref.FA) }

        LanguageOptionRow(
            text = "English",
            selected = selected == LanguagePref.EN,
        ) { onSelect(LanguagePref.EN) }
    }
}

@Composable
private fun LanguageOptionRow(
    text: String, selected: Boolean, onClick: () -> Unit
) {
    Row(Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected, onClick = onClick
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FontRow(
    selected: FontPref, onSelect: (FontPref) -> Unit
) {
    val fonts = listOf(
        FontPref.SHABNAM,
        FontPref.IRAN_SANS,
        FontPref.IRAN_NASTALIQ,
        FontPref.PELAK,
        FontPref.BOLDING,
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        fonts.forEach { font ->
            val name = font.name.replace('_', ' ')
            SettingsChip(
                label = name, selected = selected == font, onClick = { onSelect(font) })
        }
    }
}

@Composable
private fun ThemeRow(
    selected: ThemeModePref, onSelect: (ThemeModePref) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            ThemeModePref.SYSTEM to stringResource(R.string.settings_theme_system),
            ThemeModePref.LIGHT to stringResource(R.string.settings_theme_light),
            ThemeModePref.DARK to stringResource(R.string.settings_theme_dark),
        ).forEach { (mode, label) ->
            SettingsChip(
                label = label, selected = selected == mode, onClick = { onSelect(mode) })
        }
    }
}

@Composable
private fun TextScaleRow(
    selected: TextScalePref, onSelect: (TextScalePref) -> Unit
) {
    val options = listOf(
        TextScalePref.XS,
        TextScalePref.S,
        TextScalePref.M,
        TextScalePref.L,
        TextScalePref.XL,
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { scale ->
            FilterChip(
                selected = selected == scale,
                onClick = { onSelect(scale) },
                label = { Text(scale.name) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsChip(
    label: String, selected: Boolean, onClick: () -> Unit
) {
    FilterChip(
        selected = selected, onClick = onClick, label = { Text(label) })
}