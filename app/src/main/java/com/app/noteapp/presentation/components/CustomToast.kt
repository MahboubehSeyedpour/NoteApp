package com.app.noteapp.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.app.noteapp.R
import com.app.noteapp.presentation.model.ToastType


@Composable
fun CustomToast(
    message: String,
    type: ToastType,
    modifier: Modifier = Modifier
) {
    val (bgColor, icon, iconTint) = when (type) {
        ToastType.SUCCESS -> Triple(
            Color(0xFF16A34A), // green
            R.drawable.ic_circle_check,
            Color.White
        )
        ToastType.ERROR -> Triple(
            Color(0xFFDC2626), // red
            R.drawable.ic_circle_error,
            Color.White
        )
        ToastType.WARNING -> Triple(
            Color(0xFFF97316), // orange
            R.drawable.ic_circle_warning,
            Color.White
        )
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}
