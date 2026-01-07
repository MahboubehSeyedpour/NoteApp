package com.app.noteapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.noteapp.R
import com.app.noteapp.presentation.model.TagUiModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsList(
    labels: List<TagUiModel>,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = dimensionResource(R.dimen.tag_corner_round),
    horizontalGap: Dp = dimensionResource(R.dimen.dp_0),
    labelPadding: PaddingValues = PaddingValues(
        horizontal = dimensionResource(R.dimen.tag_h_padding),
        vertical = dimensionResource(R.dimen.tag_v_padding)
    ),
    onLabelClick: ((TagUiModel) -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    trailingContent: (@Composable (() -> Unit))? = null,
    selectedTagId: Long? = null,
    selectedBorderWidth: Dp = dimensionResource(R.dimen.item_selected_border_width),
    editMode: Boolean = false,
    onDeleteClick: ((TagUiModel) -> Unit)? = null,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, verticalArrangement = Arrangement.Center
    ) {

        val chipShape = RoundedCornerShape(cornerRadius)

        when {
            trailingContent != null -> trailingContent()
            trailingIcon != null && onTrailingClick != null -> {
                Box(
                    modifier = Modifier.size(dimensionResource(R.dimen.ic_button_size)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = "Add tag",
                        modifier = Modifier
                            .clickable { onTrailingClick() }
                            .background(Yellow)
                    )
                }
            }
        }

        labels.forEach { label ->

            val isSelected = (selectedTagId != null && (label.id == selectedTagId))
            val borderColor = label.color.copy(alpha = if (isSelected) 0.8f else 0.35f)
            val borderWidth = if (isSelected) selectedBorderWidth else (-1).dp
            val bgAlpha = if (isSelected) 0.12f else 0.05f

            Box(
                modifier = Modifier
                    .clip(chipShape)
                    .background(label.color.copy(alpha = bgAlpha), chipShape)
                    .border(borderWidth, borderColor, chipShape)
                    .then(if (onLabelClick != null) Modifier.clickable { onLabelClick(label) }
                    else Modifier)
                    .padding(labelPadding), contentAlignment = Alignment.Center) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(horizontalGap),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = label.name,
                        color = label.color,
                        style = MaterialTheme.typography.labelMedium
                    )

                    if (editMode && onDeleteClick != null) {
                        Box(
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.h_space_min))
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                                contentDescription = "Delete ${label.name}",
                                modifier = Modifier
                                    .size(dimensionResource(R.dimen.tag_close_icon))
                                    .clip(CircleShape)
                                    .clickable { onDeleteClick(label) },
                                tint = label.color.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

    }
}
