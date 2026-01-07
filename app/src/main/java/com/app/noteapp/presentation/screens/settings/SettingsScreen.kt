package com.app.noteapp.presentation.screens.settings

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.app.noteapp.presentation.model.iconRes
import com.app.noteapp.presentation.theme.fontFamilyFor
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController, viewModel: SettingsViewModel = hiltViewModel()
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

    Scaffold { inner ->
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
                onAvatarSelected = viewModel::onAvatarSelected,
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
    onAvatarSelected: (AvatarPref) -> Unit,
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

        // Avatar
        item {
            SettingsSection(
                title = stringResource(R.string.select_avatar_img)
            ) {
                AvatarRow(selected = uiState.avatar, onAvatarSelected = onAvatarSelected)
            }
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
                    language = uiState.language, selected = uiState.font, onSelect = onFontSelected
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
fun AvatarRow(selected: AvatarPref, onAvatarSelected: (AvatarPref) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        )
    ) {
        Column(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.v_space_min))) {

            Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))

            AvatarPickerSection(selected = selected, onSelect = { type -> onAvatarSelected(type) })

            Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String, content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
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
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabeledRadioButton(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.lang_fa),
            checked = selected == LanguagePref.FA,
            onChecked = { onSelect(LanguagePref.FA) })
        LabeledRadioButton(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.lang_en),
            checked = selected == LanguagePref.EN,
            onChecked = { onSelect(LanguagePref.EN) })
    }
}

@Composable
private fun FontRow(
    language: LanguagePref ,selected: FontPref, onSelect: (FontPref) -> Unit
) {

    val fonts = remember(language) {
        FontPref.availableFor(language)
    }

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        fonts.forEach { font ->
            val family = fontFamilyFor(font)
            Column(
                modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedSelectedItem(
                    modifier = Modifier.size(150.dp),
                    value = font,
                    selected = (selected == font),
                    onSelect = onSelect,
                    content = {
                        Text(
                            text = stringResource(R.string.lorem_ipsum),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = family
                            ),
                            textAlign = TextAlign.Center
                        )
                    })

                Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))

                Text(
                    text = stringResource(id = font.labelResId),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = family
                    ), textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ThemeRow(
    selected: ThemeModePref, onSelect: (ThemeModePref) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf(
            ThemeModePref.SYSTEM to stringResource(R.string.settings_theme_system),
            ThemeModePref.LIGHT to stringResource(R.string.settings_theme_light),
            ThemeModePref.DARK to stringResource(R.string.settings_theme_dark),
        ).forEach { (mode, label) ->
            LabeledRadioButton(
                modifier = Modifier.weight(1f),
                label = label,
                checked = selected == mode,
                onChecked = { onSelect(mode) })
        }
    }
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
                horizontal = dimensionResource(R.dimen.h_space_min),
                vertical = dimensionResource(R.dimen.v_space_min)
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding))
        ) {
            AnimatedSelectedItem(
                content = {
                    Image(
                        painter = painterResource(AvatarPref.FEMALE.iconRes()),
                        contentDescription = "item",
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.ic_avatar_size))
                            .clip(CircleShape)
                    )
                },
                selected = selected == AvatarPref.FEMALE,
                onSelect = onSelect,
                value = AvatarPref.FEMALE,
                modifier = Modifier.weight(1f)
            )
            AnimatedSelectedItem(
                content = {
                    Image(
                        painter = painterResource(AvatarPref.MALE.iconRes()),
                        contentDescription = "item",
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.ic_avatar_size))
                            .clip(CircleShape)
                    )
                },
                selected = selected == AvatarPref.MALE,
                onSelect = onSelect,
                value = AvatarPref.MALE,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))

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