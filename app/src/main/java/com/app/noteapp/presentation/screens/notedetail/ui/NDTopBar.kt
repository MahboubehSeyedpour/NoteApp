package com.app.noteapp.presentation.screens.notedetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.app.noteapp.R
import com.app.noteapp.presentation.common.components.CircularIconButton

@Composable
internal fun NDTopBar (editMode: Boolean, onChangeEditMode: (Boolean) -> Unit, saveNote: () -> Unit){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (editMode) {

            Row {
                TextButton(onClick = {
                   onChangeEditMode(false)
                    saveNote()
                }) {
                    Text(stringResource(R.string.save))
                }
                CircularIconButton(onClick = {}, icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_redo),
                        contentDescription = "Delete"
                    )
                })
                CircularIconButton(
                    onClick = {},
                    icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_undo),
                            contentDescription = "Notify"
                        )
                    },
                )
            }
        } else {
            TextButton(onClick = { onChangeEditMode(true) }) {
                Text(stringResource(R.string.edit))
            }
        }

        CircularIconButton(onClick = {}, icon = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_arrow_left),
                contentDescription = "Back"
            )
        })
    }
}