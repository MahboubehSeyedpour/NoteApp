package com.app.noteapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import com.app.noteapp.R

@Composable
fun <T> AnimatedSelectedItem(
    modifier: Modifier = Modifier,
    value: T,
    selected: Boolean,
    onSelect: (T) -> Unit,
    content: @Composable ColumnScope.(Boolean) -> Unit
) {
    val shape = RoundedCornerShape(dimensionResource(R.dimen.ic_corner_round))

    // scale animation
    val targetScale = if (selected) 1.05f else 0.8f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "animatedItemScale"
    )

    // border color
    val targetBorderColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline

    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(durationMillis = 180),
        label = "animatedItemBorderColor"
    )

    // border width
    val selectedBorderWidth = dimensionResource(R.dimen.item_selected_border_width)
    val normalBorderWidth = dimensionResource(R.dimen.dp_1)

    val borderWidth by animateDpAsState(
        targetValue = if (selected) selectedBorderWidth else normalBorderWidth,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "animatedItemBorderWidth"
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .border(borderWidth, borderColor, shape)
            .clickable { onSelect(value) }
            .padding(dimensionResource(R.dimen.v_space)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content(selected)
    }
}