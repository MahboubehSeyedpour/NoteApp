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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.R
import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.domain.model.preferences_model.FontPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import com.app.noteapp.domain.model.preferences_model.ThemeModePref
import com.app.noteapp.presentation.components.AnimatedSelectedItem
import com.app.noteapp.presentation.components.LabeledRadioButton
import com.app.noteapp.presentation.components.TextScaleSlider
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = dimensionResource(R.dimen.card_elevation)
                )
            ) {

                Column(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.v_space))) {

                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

                    AvatarPickerSection(selected = uiState.avatar, onSelect = { type -> viewModel.onAvatarSelected(type)})

                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
                }
            }
        }
    ) { inner ->
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

@OptIn(ExperimentalMaterial3Api::class)
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

        // Text scale
        item {
            SettingsSection(title = stringResource(R.string.choose_text_scale)) {
                TextScaleSlider(
                    value = uiState.textScale,
                    onValueChange = { scale -> onTextScaleSelected(scale) },
                    modifier = Modifier.fillMaxWidth()
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
            text = stringResource(R.string.lang_fa),
            selected = selected == LanguagePref.FA,
        ) { onSelect(LanguagePref.FA) }

        LanguageOptionRow(
            text = stringResource(R.string.lang_en),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsChip(
    label: String, selected: Boolean, onClick: () -> Unit
) {
    FilterChip(
        selected = selected, onClick = onClick, label = { Text(label) })
}

@Composable
fun AvatarPickerSection(
    selected: AvatarPref,
    onSelect: (AvatarPref) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.h_space),
                vertical = dimensionResource(R.dimen.v_space)
            )
    ) {
        Text(
            text = stringResource(R.string.select_avatar_img),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding))
        ) {
            AnimatedSelectedItem(
                type = AvatarPref.FEMALE,
                selected = selected == AvatarPref.FEMALE,
                onSelect = onSelect,
                modifier = Modifier.weight(1f)
            )
            AnimatedSelectedItem(
                type = AvatarPref.MALE,
                selected = selected == AvatarPref.MALE,
                onSelect = onSelect,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LabeledRadioButton(
                label = stringResource(R.string.female),
                checked = selected == AvatarPref.FEMALE,
                onChecked = { onSelect(AvatarPref.FEMALE) },
                modifier = Modifier.weight(1f)
            )
            LabeledRadioButton(
                label = stringResource(R.string.male),
                checked = selected == AvatarPref.MALE,
                onChecked = { onSelect(AvatarPref.MALE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}