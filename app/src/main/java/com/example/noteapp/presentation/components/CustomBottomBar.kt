package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.presentation.screens.home.model.NotesHomeColors
import com.example.noteapp.presentation.screens.home.model.NotesHomeMetrics
import com.example.noteapp.presentation.theme.Black

@Composable
fun CustomBottomBar(
    label: String,
    onLabelsClick: () -> Unit,
    colors: NotesHomeColors,
    metrics: NotesHomeMetrics,
    onFabClick: () -> Unit,
    fabIcon: ImageVector
) {
    Surface(color = colors.bottomBarContainer) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DarkGray)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = metrics.screenPadding)
                    .padding(bottom = 24.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onLabelsClick) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_tag),
                            contentDescription = null,
                            modifier = Modifier.size(metrics.iconSize),
                            tint = Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, color = colors.bottomBarContent)
                    }
                }
                FloatingActionButton(
                    onClick = onFabClick,
                    containerColor = colors.fabContainer,
                    contentColor = colors.fabContent,
                    shape = CircleShape
                ) {
                    Icon(fabIcon, contentDescription = "Add")
                }
            }
        }
    }
}