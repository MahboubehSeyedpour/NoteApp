package com.app.noteapp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.app.noteapp.R

@Composable
fun CircularIconButton(
    onClick: () -> Unit,
    iconSize: Dp = dimensionResource(R.dimen.ic_button_size),
    backgroundColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.35f),
    icon: @Composable () -> Unit
) {

    Surface(
        shape = CircleShape,
        color = backgroundColor,
        onClick = onClick,
        tonalElevation = dimensionResource(R.dimen.ic_button_tonal_elevation),
        shadowElevation = dimensionResource(R.dimen.ic_button_shadow_elevation)
    ) {
        Box(
            modifier = Modifier.size(iconSize),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                icon()
            }
        }
    }
}