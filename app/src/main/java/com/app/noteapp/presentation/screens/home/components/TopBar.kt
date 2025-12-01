package com.app.noteapp.presentation.screens.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.presentation.components.CustomSearchBar

@Composable
fun TopBar(
    avatar: Painter? = null,
    onAvatarClick: () -> Unit,
    query: String,
    onSearchChange: (String) -> Unit,
    onGridToggleClicked: () -> Unit,
    layoutMode: LayoutMode,
    onFilterClick: () -> Unit,
    isFilterActive: Boolean,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.screen_padding))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Avatar(avatar, onAvatarClick)
            Actions(
                query = query,
                onSearchChange = onSearchChange,
                onGridToggleClicked = onGridToggleClicked,
                layoutMode = layoutMode,
                onFilterClick = onFilterClick,
                isFilterActive = isFilterActive
            )
        }
    }
}

@Composable
fun Avatar(
    avatar: Painter? = null,
    onAvatarClick: () -> Unit,
) {
    if (avatar != null) {
        Image(
            painter = avatar,
            contentDescription = stringResource(R.string.avatar_icon),
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .clickable(onClick = onAvatarClick)
        )
    } else {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(White.copy(alpha = 0.15f))
                .clickable(onClick = onAvatarClick)
        )
    }
}

@Composable
fun Actions(
    query: String,
    onSearchChange: (String) -> Unit,
    onGridToggleClicked: () -> Unit,
    layoutMode: LayoutMode,
    onFilterClick: () -> Unit,
    isFilterActive: Boolean,
) {
    var searchExpanded by rememberSaveable { mutableStateOf(false) }

    val iconsWeight by animateFloatAsState(
        targetValue = if (searchExpanded) 0f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "iconsWeight"
    )
    val searchWeight by animateFloatAsState(
        targetValue = if (searchExpanded) 1f else 0f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "searchWeight"
    )

    val iconsAlpha by animateFloatAsState(
        targetValue = if (searchExpanded) 0f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "iconsAlpha"
    )
    val searchAlpha by animateFloatAsState(
        targetValue = if (searchExpanded) 1f else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "searchAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(searchWeight.coerceAtLeast(0.01f))
                .alpha(searchAlpha)
        ) {
            if (searchWeight > 0f) {
                CustomSearchBar(
                    value = query,
                    onValueChange = onSearchChange,
                    onSearchClick = {  },
                    onClose = {
                        onSearchChange("")
                        searchExpanded = false
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(iconsWeight.coerceAtLeast(0.01f))
                .alpha(iconsAlpha),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            if (iconsWeight > 0f) {
                IconButton(
                    onClick = { searchExpanded = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.search_note)
                    )
                }

                BadgedBox(
                    badge = {
                        if (isFilterActive) {
                            Badge(
                                modifier = Modifier.size(8.dp),
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                ) {
                    IconButton(
                        onClick = onFilterClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_filter),
                            contentDescription = stringResource(R.string.search_note)
                        )
                    }
                }

                IconButton(
                    onClick = onGridToggleClicked,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = when (layoutMode) {
                            LayoutMode.LIST -> ImageVector.vectorResource(R.drawable.ic_vertical_list)
                            LayoutMode.GRID -> ImageVector.vectorResource(R.drawable.ic_grid_list)
                        },
                        contentDescription = stringResource(R.string.toggle_layout)
                    )
                }

                IconButton(
                    onClick = { /* TODO notifications */ },
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_notification),
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
        }
    }
}

