package com.app.noteapp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.noteapp.R
import com.app.noteapp.domain.model.Note
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NoteContent(
    note: Note,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bringIntoView = remember { BringIntoViewRequester() }

    var isDescFocused by remember { mutableStateOf(false) }
    var bringJob by remember { mutableStateOf<Job?>(null) }

    fun requestBringIntoViewIfNeeded() {
        if (!isDescFocused) return
        if (bringJob?.isActive == true) bringJob?.cancel()

        bringJob = scope.launch {
            delay(250)
            bringIntoView.bringIntoView()
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .nestedScroll(rememberNestedScrollInteropConnection())
            .imePadding()
            .navigationBarsPadding()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TagFlowList(
                labels = buildList {
                note.tag?.let { add(it) }
                note.reminderTag?.let { add(it) }
            }, onLabelClick = {}, trailingIcon = null
            )

            OutlinedTextField(
                value = note.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, fontSize = 32.sp
                ),
                placeholder = {
                    Text(
                        text = context.getString(R.string.note_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold, fontSize = 32.sp
                        )
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )

            var descValue by remember { mutableStateOf(TextFieldValue(note.description.orEmpty())) }

            LaunchedEffect(note.id, note.description) {
                val newText = note.description.orEmpty()
                if (newText != descValue.text) {
                    descValue = TextFieldValue(
                        text = newText, selection = TextRange(newText.length)
                    )
                }
            }

            OutlinedTextField(
                value = descValue,
                onValueChange = { value ->
                    val newlineAdded =
                        value.text.length > descValue.text.length && value.text.lastOrNull() == '\n'

                    descValue = value
                    onDescriptionChange(value.text)

                    if (newlineAdded) requestBringIntoViewIfNeeded()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(bringIntoView)
                    .onFocusChanged { state ->
                        isDescFocused = state.isFocused
                        if (state.isFocused) requestBringIntoViewIfNeeded()
                    },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Normal, fontSize = 16.sp
                ),
                placeholder = {
                    Text(
                        context.getString(R.string.note_description),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Normal, fontSize = 16.sp
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(bringIntoView)
                    .padding(bottom = 1.dp)
            )
        }
    }
}
