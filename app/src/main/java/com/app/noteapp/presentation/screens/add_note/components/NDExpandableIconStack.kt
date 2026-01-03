package com.app.noteapp.presentation.screens.add_note.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun NDExpandableIconStack(
    modifier: Modifier = Modifier,
    icons: List<ImageVector>,
    contentDescriptions: List<String?> = List(icons.size) { null },
    onIconClick: (index: Int) -> Unit,
    iconSize: Dp = 40.dp,
    gap: Dp = 10.dp,
) {
    require(icons.isNotEmpty()) { "icons must not be empty" }
    require(contentDescriptions.size == icons.size) { "contentDescriptions size mismatch" }

    var expanded by rememberSaveable { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded, label = "stack")

    Box(
        modifier = modifier, contentAlignment = Alignment.CenterStart
    ) {
        for (i in icons.indices.reversed()) {

            val offsetX by transition.animateDp(
                label = "offset-$i", transitionSpec = {
                    tween(
                        durationMillis = 260, delayMillis = (i * 35), easing = FastOutSlowInEasing
                    )
                }) { isExpanded ->
                if (!isExpanded) 0.dp else ((iconSize + gap) * i)
            }

            val alpha by transition.animateFloat(
                label = "alpha-$i",
                transitionSpec = { tween(durationMillis = 180) }) { isExpanded ->
                if (!isExpanded && i != 0) 0f else 1f
            }

            IconButton(
                onClick = {
                    if (i == 0) expanded = !expanded
                    else onIconClick(i)
                },
                enabled = (i == 0) || expanded,
                modifier = Modifier
                    .size(iconSize)
                    .offset(x = offsetX)
                    .zIndex((icons.size - i).toFloat())
                    .graphicsLayer { this.alpha = alpha }) {
                Icon(
                    imageVector = icons[i], contentDescription = contentDescriptions[i]
                )
            }
        }
    }
}
