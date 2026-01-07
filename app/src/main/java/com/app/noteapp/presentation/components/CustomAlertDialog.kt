package com.app.noteapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.app.noteapp.R
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.DialogType

@Composable
fun CustomAlertDialog(
    spec: AppDialogSpec
) {

    val context = LocalContext.current

    val highlightColor = when (spec.type) {
        DialogType.PERMISSION -> MaterialTheme.colorScheme.primary
        DialogType.WARNING -> MaterialTheme.colorScheme.tertiary
        DialogType.ERROR -> MaterialTheme.colorScheme.error
    }

    val title = when (spec.type) {
        DialogType.PERMISSION -> stringResource(R.string.dialog_header_permission)
        DialogType.WARNING -> stringResource(R.string.dialog_header_warning)
        DialogType.ERROR -> stringResource(R.string.dialog_header_error)
    }

    Dialog(
        onDismissRequest = spec.onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = dimensionResource(R.dimen.dialog_tonal_elevation),
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.dialog_padding))
                .widthIn(
                    min = dimensionResource(R.dimen.dialog_width_min),
                    max = dimensionResource(R.dimen.dialog_width_max)
                )
                .wrapContentHeight()
        ) {
            Column(Modifier.fillMaxWidth()) {

                if (spec.showTopBar) {
                    DialogTopBar(
                        highlightColor = highlightColor,
                        onDismissRequest = spec.onDismiss,
                        title = title
                    )
                }

                spec.imageRes?.let { image ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(image)
                            .scale(Scale.FIT)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(220.dp)
                    )
                }

                Text(
                    text = stringResource(spec.messageRes),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.dialog_padding))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.dialog_padding)),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(onClick = spec.onDismiss) {
                        Text(
                            stringResource(spec.dismissTextRes),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))

                    Button(
                        onClick = spec.onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = highlightColor)
                    ) {
                        Text(stringResource(spec.confirmTextRes))
                    }
                }
            }
        }
    }
}

@Composable
fun DialogTopBar(highlightColor: Color, onDismissRequest: () -> Unit, title: String) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(highlightColor)
                .padding(dimensionResource(R.dimen.dialog_padding)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(textAlign = TextAlign.Center),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.close),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.dialog_icon_size))
                    .clickable { onDismissRequest() })

        }
    }
}
