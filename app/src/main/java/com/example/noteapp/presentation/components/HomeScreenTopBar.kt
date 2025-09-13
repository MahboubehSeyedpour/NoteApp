package com.example.noteapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.presentation.theme.Background
import com.example.noteapp.presentation.theme.Black

@Composable
fun NotesTopBar(
    config: HomeTopBarConfig,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = dimensionResource(R.dimen.screen_padding), vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (config.avatar != null) {
                Image(
                    painter = config.avatar,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.15f))
                )
            }

            Spacer(Modifier.width(12.dp))
            VerticalDivider(color=DarkGray, modifier = Modifier.padding(vertical = 13.dp))

            Spacer(Modifier.width(8.dp))

            Image(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search",
                modifier = Modifier.size(24.dp)
            )

            SearchField(
                value = config.searchText,
                onValueChange = config.onSearchChange,
                placeholder = config.placeholder,
                containerColor = Background,
                contentColor = Black,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            IconButton(onClick = config.onGridToggle) {
                Icon(config.gridToggleIcon, contentDescription = "Toggle layout")
            }

            IconButton(onClick = config.onMenuClick) {
                Icon(config.menuIcon, contentDescription = "Menu")
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = DarkGray)
    }
}