package com.app.noteapp.presentation.screens.add_note

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.core.permissions.PermissionRequest
import com.app.noteapp.core.permissions.PermissionResult
import com.app.noteapp.core.permissions.awaitPermission
import com.app.noteapp.core.permissions.rememberPermissionRequester
import com.app.noteapp.domain.model.Tag
import com.app.noteapp.presentation.components.NoteAppButton
import com.app.noteapp.presentation.components.NoteContent
import com.app.noteapp.presentation.components.NoteDetailScreenTopBar
import com.app.noteapp.presentation.components.TagFlowList
import com.app.noteapp.presentation.components.showDateTimePicker
import com.app.noteapp.presentation.theme.Background
import com.app.noteapp.R
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

    var showDeleteDialog by remember { mutableStateOf(false) }

    val requester = rememberPermissionRequester()
    var permissionError by remember { mutableStateOf<String?>(null) }

    suspend fun ensureReminderPermissions(
        requester: (PermissionRequest, (PermissionResult) -> Unit) -> Unit
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notif = awaitPermission(
                requester, PermissionRequest.Runtime(Manifest.permission.POST_NOTIFICATIONS)
            )
            if (notif != PermissionResult.GRANTED) return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val exact = awaitPermission(
                requester, PermissionRequest.Special.ScheduleExactAlarms
            )
            if (exact != PermissionResult.GRANTED) return false
        }
        return true
    }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { e ->
                when (e) {
                    is NoteDetailEvents.NavigateToHomeScreen -> navController.popBackStack()
                    is NoteDetailEvents.Error -> Toast.makeText(
                        context, e.message, Toast.LENGTH_SHORT
                    ).show()

                    NoteDetailEvents.OpenReminderPicker -> showDateTimePicker(context) { dateMillis, hour, minute ->
                        viewModel.setReminder(dateMillis, hour, minute)
                    }

                    NoteDetailEvents.RequestDeleteConfirm -> showDeleteDialog = true
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

    if (permissionError != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { permissionError = null },
            title = { Text("Permission required") },
            text = { Text(permissionError!!) },
            confirmButton = {
                TextButton(onClick = { permissionError = null }) {
                    Text("OK")
                }
            })
    }

    Scaffold(containerColor = Background, topBar = {
        Column {
            NoteDetailScreenTopBar(
                onBack = { viewModel.onBackClicked() },
                onNotificationClick = { showReminderSheet = true },
                onShareClick = {},
                onDeleteClicked = { viewModel.onDeleteClicked() })
            HorizontalDivider()
        }
    }, bottomBar = {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp)
                .animateContentSize()
                .background(Background)
        ) {
            HorizontalDivider()

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
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
            ReminderSheetContent(
                reminderText = uiState.note?.reminderTag?.name,
                onClearReminder = { viewModel.onClearReminder() },
                onPickDateTime = {
                    scope.launch {
                        val ok = ensureReminderPermissions(requester)
                        if (!ok) {
                            permissionError =
                                "We can’t schedule the reminder because required permissions were not granted."
                            return@launch
                        }
                        showReminderSheet = false
                        viewModel.openReminderPicker()
                    }
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

    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete note?") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.onConfirmDelete()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            })
    }
}

@Composable
fun ReminderSheetContent(
    reminderText: String?, onPickDateTime: () -> Unit, onClearReminder: () -> Unit
) {
    Column(
        Modifier
            .navigationBarsPadding()
            .imePadding()
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.add_reminder), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        // Pick date & time
        ListItem(
            headlineContent = { Text(stringResource(R.string.pick_date_time)) },
            leadingContent = { Icon(ImageVector.vectorResource(R.drawable.ic_calendar), null) },
            modifier = Modifier.clickable { onPickDateTime() })

        HorizontalDivider()

        // Existing reminder (if any)
        if (!reminderText.isNullOrBlank()) {
            ListItem(
                headlineContent = { Text(reminderText) },
                supportingContent = { Text(stringResource(R.string.tap_to_change)) },
                leadingContent = { Icon(ImageVector.vectorResource(R.drawable.ic_clock), null) },
                trailingContent = {
                    IconButton(
                        onClick = onClearReminder, modifier = Modifier.testTag("clear-reminder")
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_close),
                            contentDescription = stringResource(R.string.clear_reminder)
                        )
                    }
                },
                modifier = Modifier.clickable { onPickDateTime() } // tap row to edit time
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}


@Composable
fun TagSheetContent(
    tags: List<Tag>, onSelect: (Tag) -> Unit, onAdd: (String, Color) -> Unit
) {
    var newName by remember { mutableStateOf("") }
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
