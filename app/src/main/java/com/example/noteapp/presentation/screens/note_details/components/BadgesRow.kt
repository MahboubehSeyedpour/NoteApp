package com.example.noteapp.presentation.screens.note_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.presentation.components.CustomPillChip
import com.example.noteapp.presentation.theme.Primary
import com.example.noteapp.presentation.theme.SecondaryBg
import com.example.noteapp.presentation.theme.SecondaryContent
import com.example.noteapp.presentation.theme.White

@Composable
fun BadgesRow(
    categoryBadge: String?,
    timeBadge: String?,
    onCategoryClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!categoryBadge.isNullOrBlank()) {
            CustomPillChip(
                text = categoryBadge,
                containerColor = SecondaryBg,
                contentColor = SecondaryContent,
                shape = RoundedCornerShape(16.dp),
                hPad = 10.dp,
                vPad = 6.dp,
                textStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                icon = null,
                onClick = onCategoryClick
            )
        }
        if (!timeBadge.isNullOrBlank()) {
            CustomPillChip(
                text = timeBadge,
                containerColor = Primary,
                contentColor = White,
                shape = RoundedCornerShape(16.dp),
                hPad = 10.dp,
                vPad = 6.dp,
                textStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                icon = ImageVector.vectorResource(R.drawable.ic_timer),
                onClick = onTimeClick
            )
        }
    }
}