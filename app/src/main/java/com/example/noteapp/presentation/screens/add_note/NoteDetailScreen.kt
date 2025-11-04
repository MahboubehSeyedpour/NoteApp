package com.example.noteapp.presentation.screens.add_note

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.R
import com.example.noteapp.domain.model.Tag
import com.example.noteapp.presentation.components.NoteAppButton
import com.example.noteapp.presentation.components.NoteContent
import com.example.noteapp.presentation.components.NoteDetailScreenTopBar
import com.example.noteapp.presentation.components.TagFlowList
import com.example.noteapp.presentation.theme.Background
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavController, viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Sheet controls
    var showReminderSheet by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }

    val reminderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { e ->
                when (e) {
                    is NoteDetailEvents.NavigateToHomeScreen -> navController.popBackStack()
                    is NoteDetailEvents.Error -> Toast.makeText(
                        context, e.message, Toast.LENGTH_SHORT
                    ).show()

                    NoteDetailEvents.OpenReminderPicker -> TODO()
                }
            }
        }
    }

    // Back/gesture handling: close sheet first, else delegate to VM
    BackHandler(enabled = true) {
        scope.launch {
            when {
                showReminderSheet -> {
                    reminderSheetState.hide()
                    showReminderSheet = false
                }

                showTagSheet -> {
                    tagSheetState.hide()
                    showTagSheet = false
                }

                else -> viewModel.onBackClicked()
            }
        }
    }

    Scaffold(containerColor = Background, topBar = {
        Column {
            NoteDetailScreenTopBar(
                onBack = { viewModel.onBackClicked() },
                onNotificationClick = { showReminderSheet = true },
                onShareClick = {})
            HorizontalDivider()
        }
    }, bottomBar = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Background)
        ) {
            HorizontalDivider()
            Column(Modifier.padding(16.dp)) {
                Text(stringResource(R.string.choose_tag))
                TagFlowList(
                    labels = viewModel.tags.collectAsState().value,
                    cornerRadius = 18.dp,
                    horizontalGap = 18.dp,
                    verticalGap = 18.dp,
                    onLabelClick = { tag -> viewModel.onTagSelected(tag) },
                    trailingIcon = ImageVector.vectorResource(R.drawable.ic_add),
                    onTrailingClick = { showTagSheet = true },
                    selectedTagId = uiState.note?.tag?.id
                )
            }
        }
    }) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            when {
                uiState.isLoading -> Box(
                    Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                uiState.note != null -> NoteContent(
                    note = uiState.note!!,
                    onTitleChange = viewModel::updateTitle,
                    onDescriptionChange = viewModel::updateDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.note_init_failure))
                }
            }
        }
    }

    if (showReminderSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReminderSheet = false },
            sheetState = reminderSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }) {
            ReminderSheetContent(onSelectQuick = { option ->
                viewModel.applyQuickReminder(option)
                showReminderSheet = false
            }, onPickDateTime = {
                showReminderSheet = false
                viewModel.openReminderPicker()
            })
        }
    }

    if (showTagSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTagSheet = false },
            sheetState = tagSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }) {
            TagSheetContent(tags = viewModel.tags.collectAsState().value, onSelect = { tag ->
                viewModel.onTagSelected(tag)
                showTagSheet = false
            }, onAdd = { name, color ->
                viewModel.onAddTag(name, color)
                showTagSheet = false
            })
        }
    }
}

@Composable
fun ReminderSheetContent(
    onSelectQuick: (QuickReminder) -> Unit, onPickDateTime: () -> Unit
) {
    Column(
        Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.add_reminder), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        ListItem(
            headlineContent = { Text("Later today") },
            supportingContent = { Text("6:00 PM") },
            leadingContent = { Icon(ImageVector.vectorResource(R.drawable.ic_calendar), null) },
            modifier = Modifier.clickable { onSelectQuick(QuickReminder.LATER_TODAY) })
        ListItem(
            headlineContent = { Text("Tomorrow morning") },
            supportingContent = { Text("9:00 AM") },
            leadingContent = { Icon(ImageVector.vectorResource(R.drawable.ic_clock), null) },
            modifier = Modifier.clickable { onSelectQuick(QuickReminder.TOMORROW_MORNING) })
        ListItem(headlineContent = { Text("Pick date & time") }, leadingContent = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_arrow_drop_down), null
            )
        }, trailingContent = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_arrow_drop_down), null
            )
        }, modifier = Modifier.clickable { onPickDateTime() })
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun TagSheetContent(
    tags: List<Tag>, onSelect: (Tag) -> Unit, onAdd: (String, Color) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    // pick a color however you like; here, a basic preset
    val preset = listOf(
        Color(0xFF2196F3),
        Color(0xFF4CAF50),
        Color(0xFFFFC107),
        Color(0xFFF44336),
        Color(0xFF9C27B0)
    )
    var selectedColor by remember { mutableStateOf(preset.first()) }

    Column(
        Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.tags), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        TagFlowList(
            labels = tags, onLabelClick = onSelect, trailingIcon = null
        )
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.create_new), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text(stringResource(R.string.tag_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            preset.forEach { c ->
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(c)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { selectedColor = c })
            }
        }
        Spacer(Modifier.height(12.dp))
        NoteAppButton(
            text = R.string.add_tag,
            onClick = { if (newName.isNotBlank()) onAdd(newName.trim(), selectedColor) })
        Spacer(Modifier.height(12.dp))
    }
}

enum class QuickReminder { LATER_TODAY, TOMORROW_MORNING }

