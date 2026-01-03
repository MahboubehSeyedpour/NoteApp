package com.app.noteapp.presentation.screens.add_note.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.app.noteapp.R
import com.app.noteapp.presentation.components.NoteTag
import com.app.noteapp.presentation.components.TagsList
import com.app.noteapp.presentation.model.TagUiModel

@Composable
fun TagSheetContent(
    tags: List<TagUiModel>, onSelect: (TagUiModel) -> Unit, onAdd: (String, Color) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    val preset = listOf(
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFFFC107),
        Color(0xFFF44336),
        Color(0xFF9C27B0)
    )
    var selectedColor by remember { mutableStateOf(preset.first()) }

    Column(
        Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(
                horizontal = dimensionResource(R.dimen.btm_sheet_h_padding),
                vertical = dimensionResource(R.dimen.btm_sheet_v_padding)
            )
    ) {
        Text(stringResource(R.string.tags), style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        TagsList(
            labels = tags, onLabelClick = onSelect, trailingIcon = null
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text(stringResource(R.string.tag_name)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(dimensionResource(R.dimen.dp_1), MaterialTheme.colorScheme.primary),
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            preset.forEach { c ->
                Box(
                    Modifier
                        .size(
                            if (c == selectedColor) dimensionResource(R.dimen.tag_size_on_select) else dimensionResource(
                                R.dimen.tag_size_normal
                            )
                        )
                        .clip(CircleShape)
                        .background(c)
                        .border(
                            width = if (c == selectedColor) dimensionResource(R.dimen.dp_6) else dimensionResource(
                                R.dimen.dp_1
                            ),
                            color = if (c == selectedColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = c }, contentAlignment = Alignment.Center
                ) {}
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

//        NoteAppButton(
//            modifier = Modifier.fillMaxWidth(),
//            text = R.string.add_tag,
//            onClick = { if (newName.isNotBlank()) onAdd(newName.trim(), selectedColor) })
//
//        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
    }
}

fun generatePaletteFromSeed(
    seed: Color, count: Int = 24
): List<Color> {
    // Generate hues around the seed hue
    val seedHsl = FloatArray(3)
    ColorUtils.colorToHSL(seed.toArgb(), seedHsl)
    val seedHue = seedHsl[0]

    val colors = mutableListOf<Color>()
    val step = 360f / count

    for (i in 0 until count) {
        val h = (seedHue + i * step) % 360f
        val s = 0.65f
        val l = 0.55f
        val argb = ColorUtils.HSLToColor(floatArrayOf(h, s, l))
        colors += Color(argb)
    }

    return colors
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorPalettePicker(
    colors: List<Color>,
    selected: Color,
    onSelect: (Color) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 8
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 6.dp)
    ) {
        items(items = colors, key = { it.value.toString() }) { c ->
            val selectedNow = c == selected
            Box(
                modifier = Modifier
                    .size(if (selectedNow) 34.dp else 28.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(
                        width = if (selectedNow) 4.dp else 1.dp,
                        color = if (selectedNow) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { onSelect(c) })
        }
    }
}

@Composable
fun TagsSection(tag: TagUiModel?, reminderTag: TagUiModel?) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding)),
        verticalArrangement = Arrangement.Center
    ) {
        tag?.let {
            NoteTag(it, icon = R.drawable.ic_hashtag)
        }

        reminderTag?.let {
            NoteTag(it, icon = R.drawable.ic_clock)
        }
    }
}