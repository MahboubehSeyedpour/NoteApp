package com.app.noteapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.noteapp.R

@Composable
fun CustomSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onClose: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = shape,
        singleLine = true,
        leadingIcon = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_note)
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    onValueChange("")
                    onClose()
                }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.close)
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search_note),
                style = TextStyle.Default.copy (
                    fontSize = 12.sp
                )
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        textStyle = TextStyle.Default.copy (
            fontSize = 12.sp
        )
    )
}