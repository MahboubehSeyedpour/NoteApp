package com.app.noteapp.presentation.screens.notedetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.noteapp.R
import com.app.noteapp.data.local.model.enums.MediaKind
import com.app.noteapp.presentation.model.NoteBlockUiModel

@Composable
fun NDMediaBlockItem(
    block: NoteBlockUiModel.Media, onDelete: () -> Unit, editMode: Boolean
) {
    val context = LocalContext.current

    when (block.kind) {
        MediaKind.IMAGE -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.icon_size))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(block.localUri).crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 320.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                if (editMode) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
        }

        MediaKind.VIDEO -> {
            NDMediaChip(
                icon = ImageVector.vectorResource(R.drawable.ic_play),
                label = "Video",
                uri = block.localUri,
                durationMs = block.durationMs,
                onDelete = onDelete,
                editMode = editMode
            )
        }

        MediaKind.AUDIO -> {
            NDMediaChip(
                icon = ImageVector.vectorResource(R.drawable.ic_play),
                label = "Audio",
                uri = block.localUri,
                durationMs = block.durationMs,
                onDelete = onDelete,
                editMode = editMode
            )
        }
    }
}