package com.app.noteapp.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.noteapp.R
import com.app.noteapp.domain.model.preferences_model.TextScalePref
import kotlin.math.roundToInt

private val TrackHeight = 6.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextScaleSlider(
    value: TextScalePref,
    onValueChange: (TextScalePref) -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        TextScalePref.XS,
        TextScalePref.S,
        TextScalePref.M,
        TextScalePref.L,
        TextScalePref.XL
    )
    val labels = listOf("XS", "S", "M", "L", "XL")

    val currentIndex = steps.indexOf(value).coerceAtLeast(0)
    val maxIndex = steps.lastIndex

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Slider(
            value = currentIndex.toFloat(),
            onValueChange = { raw ->
                val idx = raw.roundToInt().coerceIn(0, maxIndex)
                val newScale = steps[idx]
                if (newScale != value) onValueChange(newScale)
            },
            valueRange = 0f..maxIndex.toFloat(),
            steps = maxIndex - 1,
            thumb = {
                ThumbIcon()
            },
            track = { sliderState ->
                TrackWithDots(
                    sliderState = sliderState,
                    stepsCount = steps.size
                )
            }
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.icon_size) / 2),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { index, label ->
                val selected = currentIndex == index
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ThumbIcon(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = dimensionResource(R.dimen.icon_size) ,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = size *1.5f, minHeight = size * 1.5f)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_like),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size * 0.9f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackWithDots(
    sliderState: SliderState,
    stepsCount: Int,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.background,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = TrackHeight,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(trackColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(stepsCount) { index ->
                val selected = sliderState.value.roundToInt() == index
                Box(
                    modifier = Modifier
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) dotColor
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}