package com.example.noteapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapp.domain.model.Tag


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagFlowList(
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
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalGap),
        verticalArrangement = Arrangement.spacedBy(verticalGap)
    ) {
        labels.forEach { label ->
            val chipShape = RoundedCornerShape(cornerRadius)
            val chip = @Composable {
                Box(
                    modifier = Modifier
                        .clip(chipShape)
                        .background(label.color.copy(alpha = 0.05f), chipShape)
                        .border(0.dp, label.color.copy(alpha = 0.35f), chipShape)
                        .padding(labelPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label.name,
                        color = label.color,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            if (onLabelClick != null) {
                Box(Modifier.clickable { onLabelClick(label) }) { chip() }
            } else {
                chip()
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
                ) {
                    Icon(trailingIcon, contentDescription = "Add tag")
                }
            }
        }
    }
}
