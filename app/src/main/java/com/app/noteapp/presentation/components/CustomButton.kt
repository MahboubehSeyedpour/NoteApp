package com.app.noteapp.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.app.noteapp.R
import com.app.noteapp.presentation.theme.LocalAppShapes

@Composable
fun NoteAppButton(modifier: Modifier = Modifier, text: Int, onClick: () -> Unit) {
    Button(
        modifier = modifier
            .height(dimensionResource(R.dimen.button_height)),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
        ), shape = LocalAppShapes.current.chip, onClick = onClick
    ) {
        Text(text = stringResource(text))
    }
}