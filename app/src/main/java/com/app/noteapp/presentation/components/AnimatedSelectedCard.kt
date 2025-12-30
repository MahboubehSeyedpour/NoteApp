package com.app.noteapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.app.noteapp.R
import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.presentation.model.iconRes

@Composable
fun AnimatedSelectedItem(
    modifier: Modifier,
    type: AvatarPref,
    selected: Boolean,
    onSelect: (AvatarPref) -> Unit
) {
    val shape = RoundedCornerShape(dimensionResource(R.dimen.ic_corner_round))

    // --- scale animation
    val targetScale = if (selected) 1.05f else 0.8f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "avatarScale"
    )

    // --- border color animation ---
    val targetBorderColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline

    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(durationMillis = 180),
        label = "avatarBorderColor"
    )

    // --- border width animation ---
    val selectedBorderWidth = dimensionResource(R.dimen.item_selected_border_width)
    val normalBorderWidth = dimensionResource(R.dimen.dp_1)

    val borderWidth by animateDpAsState(
        targetValue = if (selected) selectedBorderWidth else normalBorderWidth,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "avatarBorderWidth"
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .border(borderWidth, borderColor, shape)
            .clickable { onSelect(type) }
            .padding(dimensionResource(R.dimen.v_space)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(type.iconRes()),
            contentDescription = type.name,
            modifier = Modifier
                .size(dimensionResource(R.dimen.ic_avatar_size))
                .clip(CircleShape)
        )
    }
}