package com.example.noteapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import com.example.noteapp.presentation.theme.LocalAppShapes
import com.example.noteapp.presentation.theme.Primary
import com.example.noteapp.presentation.theme.White

@Composable
fun NoteAppButton(text: Int, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .padding(vertical = dimensionResource(R.dimen.screen_padding))
            .fillMaxWidth()
            .height(58.dp),
        colors = ButtonColors(
            containerColor = Primary,
            contentColor = White,
            disabledContainerColor = Primary,
            disabledContentColor = White
        ),
        shape = LocalAppShapes.current.chip,
        onClick = onClick
    ) {
        Text(text = stringResource(text))
    }
}