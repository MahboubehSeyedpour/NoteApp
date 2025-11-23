package com.app.noteapp.presentation.components

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.noteapp.R
import com.app.noteapp.domain.model.Tag


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsList(
    labels: List<Tag>,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    horizontalGap: Dp = 8.dp,
    verticalGap: Dp = 8.dp,
    labelPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    onLabelClick: ((Tag) -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    trailingContent: (@Composable (() -> Unit))? = null,
    selectedTagId: Long? = null,
    selectedBorderWidth: Dp = 4.dp,
    editMode: Boolean = false,
    onDeleteClick: ((Tag) -> Unit)? = null,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalGap),
        verticalArrangement = Arrangement.spacedBy(verticalGap)
    ) {
        labels.forEach { label ->
            val chipShape = RoundedCornerShape(cornerRadius)
            val isSelected = (selectedTagId != null && (label.id == selectedTagId))
            val borderWidth = if (isSelected) selectedBorderWidth else (-1).dp
            val bgAlpha = if (isSelected) 0.12f else 0.05f
            val borderColor = label.color.copy(alpha = if (isSelected) 0.8f else 0.35f)

            Box(
                modifier = Modifier
                    .clip(chipShape)
                    .background(label.color.copy(alpha = bgAlpha), chipShape)
                    .border(borderWidth, borderColor, chipShape)
                    .then(if (onLabelClick != null) Modifier.clickable { onLabelClick(label) }
                    else Modifier)
                    .padding(labelPadding), contentAlignment = Alignment.Center) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = label.name,
                        color = label.color,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium, fontSize = 12.sp
                        )
                    )

                    if (editMode && onDeleteClick != null) {
                        Box(
                            modifier = Modifier.padding(start = 6.dp)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                                contentDescription = "Delete ${label.name}",
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .clickable { onDeleteClick(label) },
                                tint = label.color.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        when {
            trailingContent != null -> trailingContent()
            trailingIcon != null && onTrailingClick != null -> {
                IconButton(
                    onClick = onTrailingClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(cornerRadius))
                        .testTag("tag-add")
                ) { Icon(trailingIcon, contentDescription = "Add tag") }
            }
        }
    }
}
