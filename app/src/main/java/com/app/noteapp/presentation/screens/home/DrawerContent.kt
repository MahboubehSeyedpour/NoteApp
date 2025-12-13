package com.app.noteapp.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.noteapp.R
import com.app.noteapp.domain.common_model.AppFont
import com.app.noteapp.domain.common_model.AppLanguage
import com.app.noteapp.domain.common_model.AvatarType
import com.app.noteapp.presentation.model.iconRes

@Composable
fun DrawerContent(
    currentAvatar: AvatarType,
    onAvatarSelected: (AvatarType) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    currentFont: AppFont,
    onFontSelected: (AppFont) -> Unit,
    onExportClicked: () -> Unit,
    onImportClicked: () -> Unit,
) {
    ModalDrawerSheet {
        AvatarPickerSection(selected = currentAvatar, onSelect = onAvatarSelected)

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        HorizontalDivider()
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        LanguagePickerSection(selected = currentLanguage, onSelect = onLanguageSelected)

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        HorizontalDivider()
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        FontPickerSection(
            selected = currentFont, onSelect = onFontSelected
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        HorizontalDivider()
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        ExportSection(onExportClicked = onExportClicked)

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        HorizontalDivider()
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        ImportSection(onImportClicked = onImportClicked)
    }
}

@Composable
fun AvatarPickerSection(
    selected: AvatarType,
    onSelect: (AvatarType) -> Unit,
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
            text = stringResource(R.string.choose_avatar_img),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding))
        ) {
            AvatarOption(
                type = AvatarType.FEMALE,
                selected = selected == AvatarType.FEMALE,
                onSelect = onSelect,
                modifier = Modifier.weight(1f)
            )
            AvatarOption(
                type = AvatarType.MALE,
                selected = selected == AvatarType.MALE,
                onSelect = onSelect,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AvatarRadio(
                label = stringResource(R.string.female),
                checked = selected == AvatarType.FEMALE,
                onChecked = { onSelect(AvatarType.FEMALE) },
                modifier = Modifier.weight(1f)
            )
            AvatarRadio(
                label = stringResource(R.string.male),
                checked = selected == AvatarType.MALE,
                onChecked = { onSelect(AvatarType.MALE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AvatarOption(
    modifier: Modifier, type: AvatarType, selected: Boolean, onSelect: (AvatarType) -> Unit
) {
    val borderColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val borderWidth =
        if (selected) dimensionResource(R.dimen.item_selected_border_width) else dimensionResource(R.dimen.dp_1)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(R.dimen.ic_corner_round)))
            .border(
                borderWidth,
                borderColor,
                RoundedCornerShape(dimensionResource(R.dimen.ic_corner_round))
            )
            .clickable { onSelect(type) }
            .padding(dimensionResource(R.dimen.v_space)),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(type.iconRes()),
            contentDescription = type.name,
            modifier = Modifier
                .size(dimensionResource(R.dimen.ic_avatar_size))
                .clip(CircleShape)
        )
    }
}

@Composable
private fun AvatarRadio(
    modifier: Modifier, label: String, checked: Boolean, onChecked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable { onChecked() }) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            RadioButton(
                selected = checked, onClick = onChecked
            )
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun LanguagePickerSection(
    selected: AppLanguage, onSelect: (AppLanguage) -> Unit
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
            text = stringResource(R.string.choose_language),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LanguageRadio(
                label = stringResource(R.string.lang_fa),
                checked = selected == AppLanguage.FA,
                onChecked = { onSelect(AppLanguage.FA) },
                modifier = Modifier.weight(1f)
            )
            LanguageRadio(
                label = stringResource(R.string.lang_en),
                checked = selected == AppLanguage.EN,
                onChecked = { onSelect(AppLanguage.EN) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LanguageRadio(
    label: String, checked: Boolean, onChecked: () -> Unit, modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable { onChecked() }) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) { // remove radio button padding
            RadioButton(selected = checked, onClick = onChecked)
        }
        Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontPickerSection(
    selected: AppFont,
    onSelect: (AppFont) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val iranNastaliq = stringResource(R.string.iran_nastaliq)
    val iranSans = stringResource(R.string.iran_sans)
    val pelak = stringResource(R.string.pelak)
    val shabnam = stringResource(R.string.shabnam)
    val bromlis = stringResource(R.string.boomlis)
    val schoolPlanner = stringResource(R.string.school_planner)
    val bolding = stringResource(R.string.bolding)

    val fontItems = listOf(
        AppFont.IRAN_NASTALIQ to iranNastaliq,
        AppFont.IRAN_SANS to iranSans,
        AppFont.PELAK to pelak,
        AppFont.SHABNAM to shabnam,
        AppFont.BROMLIS_REGULAR to bromlis,
        AppFont.BOLDING to schoolPlanner,
        AppFont.SCHOOL_PLANNER to bolding,
    )

    val selectedLabel = fontItems.firstOrNull { it.first == selected }?.second.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.h_space),
                vertical = dimensionResource(R.dimen.v_space)
            )
    ) {
        Text(
            text = stringResource(R.string.choose_font),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        fieldWidth = with(density) { coordinates.size.width.toDp() }
                    }
                    .clickable { expanded = !expanded },
                shape = RoundedCornerShape(dimensionResource(R.dimen.tag_corner_round)),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = BorderStroke(
                    width = dimensionResource(R.dimen.dp_1),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                ),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = dimensionResource(R.dimen.h_space),
                            vertical = dimensionResource(R.dimen.tag_v_padding)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedLabel.ifBlank { stringResource(R.string.pelak) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = if (expanded) ImageVector.vectorResource(R.drawable.ic_arrow_up)
                        else ImageVector.vectorResource(R.drawable.ic_arrow_down),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }, offset = DpOffset(
                    x = 0.dp, y = dimensionResource(R.dimen.v_space)
                ), // space between Surface and menu
                modifier = Modifier
                    .width(fieldWidth)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                fontItems.forEach { (font, label) ->
                    DropdownMenuItem(text = {
                        Text(
                            text = label, style = MaterialTheme.typography.bodyMedium
                        )
                    }, onClick = {
                        expanded = false
                        if (font != selected) {
                            onSelect(font)
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun ExportSection(onExportClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.h_space),
                vertical = dimensionResource(R.dimen.v_space),
            ), verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.export_to))
            Button(onClick = onExportClicked) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_export), contentDescription = "export"
                )
            }
        }
        Text(
            stringResource(R.string.export_description),
            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.secondary)
        )
    }
}

@Composable
fun ImportSection(onImportClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.h_space),
                vertical = dimensionResource(R.dimen.v_space),
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.import_from))
        Button(onClick = onImportClicked) {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_import), contentDescription = "import"
            )
        }
    }
}

