package com.app.noteapp.presentation.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.app.noteapp.R

@Composable
fun LabelCircularIconButton(
    onClick: () -> Unit,
    iconSize: Dp = dimensionResource(R.dimen.ic_button_size),
    backgroundColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.35f),
    icon: @Composable () -> Unit,
    label: String
) {

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        onClick = onClick,
        tonalElevation = dimensionResource(R.dimen.ic_button_tonal_elevation),
        shadowElevation = dimensionResource(R.dimen.ic_button_shadow_elevation)
    ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(iconSize)) {
                Text(label, style = MaterialTheme.typography.labelLarge)
                icon()
            }
    }
}