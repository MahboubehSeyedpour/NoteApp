package com.app.noteapp.presentation.components

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode

@Composable
fun TopBar(
    avatar: Painter? = null,
    onAvatarClick: () -> Unit,
    query: String,
    onSearchChange: (String) -> Unit,
    onGridToggleClicked: () -> Unit,
    layoutMode: LayoutMode,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
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
                .height(dimensionResource(R.dimen.topbar_height)).padding(end = dimensionResource(R.dimen.icon_size)/2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Avatar(avatar, onAvatarClick)
            Text(text = stringResource(R.string.app_name))
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.topbar_height)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CircularIconButton(
                onClick = onGridToggleClicked, icon = {
                    Icon(
                        imageVector = when (layoutMode) {
                            LayoutMode.LIST -> ImageVector.vectorResource(R.drawable.ic_vertical_list)
                            LayoutMode.GRID -> ImageVector.vectorResource(R.drawable.ic_grid_list)
                        }, contentDescription = stringResource(R.string.toggle_layout)
                    )
                })

            Actions(
                query = query,
                onSearchChange = onSearchChange,
                onFilterClick = onFilterClick,
                onSortClick = onSortClick,
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
        CircularIconButton(onClick = { onAvatarClick() }, icon = {
            Image(
                painter = avatar,
                contentDescription = stringResource(R.string.avatar_icon),
            )
        })
    } else {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.icon_size))
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
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
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
            ), verticalAlignment = Alignment.CenterVertically
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
                    onSearchClick = { },
                    onClose = {
                        onSearchChange("")
                        searchExpanded = false
                    })
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                LabelCircularIconButton (
                    label = R.string.sort,
                    onClick = onSortClick, icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_down),
                            contentDescription = stringResource(R.string.sort)
                        )
                    })
            }


            BadgedBox(
                badge = {
                    if (isFilterActive) {
                        Badge(
                            modifier = Modifier.size(dimensionResource(R.dimen.badge_size)),
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    }
                }) {
                CircularIconButton(
                    onClick = onFilterClick, icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_filter),
                            contentDescription = stringResource(R.string.search_note)
                        )
                    })
            }

            if (iconsWeight > 0f) {
                CircularIconButton(onClick = { searchExpanded = true }, icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.search_note)
                    )
                })
            }

//            CircularIconButton(
//                onClick = onGridToggleClicked, icon = {
//                    Icon(
//                        imageVector = when (layoutMode) {
//                            LayoutMode.LIST -> ImageVector.vectorResource(R.drawable.ic_vertical_list)
//                            LayoutMode.GRID -> ImageVector.vectorResource(R.drawable.ic_grid_list)
//                        }, contentDescription = stringResource(R.string.toggle_layout)
//                    )
//                })
        }

//        CircularIconButton(onClick = { /* TODO notifications */ }, icon = {
//            Icon(
//                imageVector = ImageVector.vectorResource(R.drawable.ic_notification),
//                contentDescription = stringResource(R.string.notification)
//            )
//        })
    }
}

