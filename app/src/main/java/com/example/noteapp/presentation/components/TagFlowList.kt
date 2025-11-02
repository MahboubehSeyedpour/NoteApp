package com.example.noteapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    verticalGap: Dp = 8.dp
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(horizontalGap),
        verticalArrangement = Arrangement.spacedBy(verticalGap)
    ) {
        labels.forEach { label ->
            Box(
                modifier = Modifier
                    .background(
                        color = label.color.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
//                    .border(
//                        width = 1.dp,
//                        color = label.color,
//                        shape = RoundedCornerShape(cornerRadius)
//                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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
    }
}
