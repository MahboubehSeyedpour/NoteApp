package com.app.noteapp.presentation.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.noteapp.R
import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.presentation.components.CustomSearchField

@Composable
fun TopBar(
    avatar: Painter? = null,
    onAvatarClick: () -> Unit,
    query: String,
    onSearchChange: (String) -> Unit,
    onGridToggleClicked: () -> Unit,
    layoutMode: LayoutMode,
) {

    Column {
        AppNameAndAvatarRow(avatar, onAvatarClick)
        SearchAndToggleRow(query, onSearchChange, onGridToggleClicked, layoutMode)
    }
}

@Composable
fun AppNameAndAvatarRow(
    avatar: Painter? = null,
    onAvatarClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.my_notes),
                style = TextStyle.Default.copy(fontSize = 28.sp),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), contentAlignment = Alignment.CenterEnd
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
    }
}

@Composable
fun SearchAndToggleRow(
    query: String,
    onSearchChange: (String) -> Unit,
    onGridToggleClicked: () -> Unit,
    layoutMode: LayoutMode,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.screen_padding))
    ) {
        CustomSearchField(
            value = query,
            onValueChange = onSearchChange,
            placeholder = stringResource(R.string.search_note),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RectangleShape,
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
        )

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
                }, contentDescription = stringResource(R.string.toggle_layout)
            )
        }
    }
}