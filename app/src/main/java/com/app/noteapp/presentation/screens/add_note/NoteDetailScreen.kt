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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.R
import com.app.noteapp.core.permissions.PermissionRequest
import com.app.noteapp.core.permissions.PermissionResult
import com.app.noteapp.core.permissions.awaitPermission
import com.app.noteapp.core.permissions.rememberPermissionRequester
import com.app.noteapp.domain.model.Tag
import com.app.noteapp.presentation.components.CircularIconButton
import com.app.noteapp.presentation.components.CustomAlertDialog
import com.app.noteapp.presentation.components.NoteAppButton
import com.app.noteapp.presentation.components.NoteContent
import com.app.noteapp.presentation.components.NoteDetailScreenTopBar
import com.app.noteapp.presentation.components.TagsList
import com.app.noteapp.presentation.components.showDateTimePicker
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavController, viewModel: NoteDetailViewModel = hiltViewModel()
) {

    AppTheme.extended

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
    var permissionError by remember { mutableStateOf<Int?>(null) }

    var editMode by remember { mutableStateOf(false) }
    var tagToDelete by remember { mutableStateOf<Tag?>(null) }

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
        CustomAlertDialog(
            type = DialogType.ERROR,
            onDismissRequest = { permissionError = null },
            title = stringResource(R.string.permission_required),
            message = stringResource(permissionError!!),
            onConfirmBtnClick = { permissionError = null },
            confirmBtnText = R.string.ok,
            dismissBtnText = R.string.no,
            onDismissButtonClick = {},
            showTopBar = true
        )
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.screen_padding)),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                NoteDetailScreenTopBar(
                    onBack = { viewModel.onBackClicked() },
                    onNotificationClick = { showReminderSheet = true },
                    onShareClick = {},
                    onDeleteClicked = { viewModel.onDeleteClicked() })
                HorizontalDivider(
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.screen_padding)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                )
            }
        },
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
                    .animateContentSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                )

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(R.dimen.screen_padding)),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.choose_tag),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        CircularIconButton(onClick = { editMode = !editMode }, icon = {
                            Icon(
                                ImageVector.vectorResource(R.drawable.ic_edit),
                                contentDescription = "Edit"
                            )
                        })
                    }

                    TagsList(
                        labels = viewModel.tags.collectAsState().value,
                        cornerRadius = 18.dp,
                        horizontalGap = 18.dp,
                        verticalGap = 18.dp,
                        onLabelClick = { tag -> viewModel.onTagSelected(tag) },
                        trailingIcon = ImageVector.vectorResource(R.drawable.ic_add),
                        onTrailingClick = { showTagSheet = true },
                        selectedTagId = uiState.note?.tag?.id,
                        editMode = editMode,
                        onDeleteClick = { tag -> tagToDelete = tag })

                    if (tagToDelete != null) {
                        CustomAlertDialog(
                            type = DialogType.WARNING,
                            onDismissRequest = { tagToDelete = null },
                            title = stringResource(R.string.delete_tag),
                            message = stringResource(
                                R.string.delete_tag_confirm,
                                tagToDelete!!.name
                            ),
                            onConfirmBtnClick = {
                                viewModel.onDeleteTag(tagToDelete!!.id)
                                tagToDelete = null
                            },
                            confirmBtnText = R.string.delete,
                            onDismissButtonClick = { tagToDelete = null },
                            dismissBtnText = R.string.no,
                            showTopBar = true
                        )
                    }
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
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            ReminderSheetContent(
                reminderText = uiState.note?.reminderTag?.name,
                onClearReminder = { viewModel.onClearReminder() },
                onPickDateTime = {
                    scope.launch {
                        val ok = ensureReminderPermissions(requester)
                        if (!ok) {
                            permissionError = R.string.permission_required_message
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
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.background
        ) {
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
        CustomAlertDialog(
            type = DialogType.ERROR,
            onDismissRequest = { showDeleteDialog = false },
            title = stringResource(R.string.delete_note),
            message = stringResource(R.string.delete_note_question),
            onConfirmBtnClick = {
                showDeleteDialog = false
                viewModel.onConfirmDelete()
            },
            confirmBtnText = R.string.delete,
            onDismissButtonClick = { showDeleteDialog = false },
            dismissBtnText = R.string.dialog_dismiss_btn,
            showTopBar = true
        )
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

        HorizontalDivider(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.35f
                )
            )
        )

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
        Spacer(Modifier.height(16.dp))
        TagsList(
            labels = tags, onLabelClick = onSelect, trailingIcon = null
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text(stringResource(R.string.tag_name)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary),
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            preset.forEach { c ->
                Box(
                    Modifier
                        .size(if (c == selectedColor) 36.dp else 28.dp)
                        .clip(CircleShape)
                        .background(c)
                        .border(
                            width = if (c == selectedColor) 5.dp else 1.dp,
                            color = if (c == selectedColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = c },
                    contentAlignment = Alignment.Center
                ){}
            }
        }
        Spacer(Modifier.height(16.dp))
        NoteAppButton(
            text = R.string.add_tag,
            onClick = { if (newName.isNotBlank()) onAdd(newName.trim(), selectedColor) })
        Spacer(Modifier.height(16.dp))
    }
}
