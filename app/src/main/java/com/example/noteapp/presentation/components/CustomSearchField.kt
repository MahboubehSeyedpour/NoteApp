package com.example.noteapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.noteapp.R

@Composable
fun CustomSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp) // soft corners
            )
            .clip(RoundedCornerShape(8.dp)) // ensures content stays inside border
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .heightIn(min = 56.dp)
                .testTag("search-field"),
            placeholder = {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.search_icon),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = placeholder,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                }
            },
            singleLine = true,
            shape = shape,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = containerColor,
                focusedContainerColor = containerColor,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = contentColor,
                focusedTextColor = contentColor,
                unfocusedTextColor = contentColor
            ),
            textStyle = TextStyle(
                textAlign = TextAlign.Start
            )
        )
    }
}