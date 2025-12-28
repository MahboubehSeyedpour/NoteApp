package com.app.noteapp.presentation.components

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.noteapp.R
import com.app.noteapp.data.local.model.enums.MediaKind
import com.app.noteapp.presentation.model.NoteBlockUiModel
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.TagUiModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomNoteCard(
    note: NoteUiModel,
    isSelected: Boolean,
    onNoteClicked: () -> Unit,
    onNotePinned: () -> Unit,
    deleteNote: () -> Unit,
    noteTitleStyle: TextStyle,
    noteBodyStyle: TextStyle,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNoteClicked() },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                MenuButton(onNotePinned, deleteNote)

                Text(
                    modifier = Modifier.weight(1f),
                    text = note.title,
                    style = noteTitleStyle.copy(textAlign = TextAlign.Start)
                )
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.v_space)*3))


            NotePreviewBlocks(
                blocks = note.blocks,
                textStyle = noteBodyStyle
            )

            Spacer(Modifier.height(dimensionResource(R.dimen.v_space)*3))

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.icon_size)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding)),
                verticalArrangement = Arrangement.Center
            ) {
//                note.tag?.let {
//                    NoteTag(it, icon = R.drawable.ic_hashtag)
//                }
//
//                note.reminderTag?.let {
//                    NoteTag(it, icon = R.drawable.ic_clock)
//                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
        }
    }
}

@Composable
fun NoteTag(tagUiModel: TagUiModel, @DrawableRes icon: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        val chipShape = RoundedCornerShape(dimensionResource(R.dimen.tag_corner_round))

        Box(
            modifier = Modifier
                .clip(chipShape)
                .background(tagUiModel.color.copy(alpha = 0.05f), chipShape)
                .border((-1).dp, tagUiModel.color.copy(alpha = 0.8f), chipShape)
                .padding(
                    PaddingValues(
                        horizontal = dimensionResource(R.dimen.tag_h_padding),
                        vertical = dimensionResource(R.dimen.tag_v_padding)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.dp_0)),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(dimensionResource(R.dimen.icon_size))
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(icon),
                        contentDescription = null,
                        tint = tagUiModel.color.copy(alpha = 0.8f),
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_size) / 2)
                    )

                    Spacer(Modifier.width(dimensionResource(R.dimen.h_space)/2))

                    Text(
                        text = tagUiModel.name,
                        color = tagUiModel.color,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun MenuButton(pinNote: () -> Unit, deleteNote: () -> Unit) {

    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_options),
                contentDescription = stringResource(R.string.more_options),
            )
        }

        DropdownMenu(
            expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            DropdownMenuItem(text = { Text(stringResource(R.string.pin)) }, onClick = {
                menuExpanded = false
                pinNote()
            })
            DropdownMenuItem(text = { Text(stringResource(R.string.delete)) }, onClick = {
                menuExpanded = false
                deleteNote()
            })
        }
    }
}

@Composable
private fun NotePreviewBlocks(
    blocks: List<NoteBlockUiModel>,
    textStyle: TextStyle,
) {
    if (blocks.isEmpty()) return

    val first = blocks.first()

    val horizontalPadding = dimensionResource(R.dimen.icon_size)

    when (first) {
        is NoteBlockUiModel.Text -> {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                text = first.text,
                style = textStyle,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }

        is NoteBlockUiModel.Media -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
            ) {

                if (first.kind == MediaKind.IMAGE) {
                    AsyncImage(
                        model = first.localUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.tag_corner_round))),
                        contentScale = ContentScale.Crop
                    )
                } else {

                }

                val second = blocks.getOrNull(1)
                if (second is NoteBlockUiModel.Text) {
                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
                    Text(
                        text = second.text,
                        style = textStyle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}
