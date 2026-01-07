package com.app.noteapp.presentation.screens.add_note

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.app.noteapp.R
import com.app.noteapp.core.time.formatUnixMillis
import com.app.noteapp.presentation.components.CircularIconButton
import com.app.noteapp.presentation.components.CustomAlertDialog
import com.app.noteapp.presentation.components.ToastHost
import com.app.noteapp.presentation.components.showDateTimePicker
import com.app.noteapp.presentation.model.AppDialogSpec
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.model.NoteBlockUiModel
import com.app.noteapp.presentation.model.ToastUI
import com.app.noteapp.presentation.screens.add_note.components.NDExpandableIconStack
import com.app.noteapp.presentation.screens.add_note.components.NDMediaBlockItem
import com.app.noteapp.presentation.screens.add_note.components.NDTextBlockItem
import com.app.noteapp.presentation.screens.add_note.components.NDTopBar
import com.app.noteapp.presentation.screens.permission_manager.openAppSettingsInDevice
import com.app.noteapp.presentation.screens.permission_manager.rememberPermissionLauncher
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavController, viewModel: NoteDetailViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
//    var showReminderSheet by remember { mutableStateOf(false) }
//    var showTagSheet by remember { mutableStateOf(false) }
//    val reminderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    val tagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
//    var permissionError by remember { mutableStateOf<Int?>(null) }
    var toastMessage by remember { mutableStateOf<ToastUI?>(null) }
    var activeDialog by remember { mutableStateOf<AppDialogSpec?>(null) }

    // -------------------------------- photo picker --------------------------------
    val result = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        result.value = it
    }

    val requestPermission = rememberPermissionLauncher(
        onGranted = { launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onDenied = {
//            permissionError = R.string.permission_denied
                        }
    )
    // --------------------------------------------------------------------------------

    LaunchedEffect(viewModel.events) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { event ->
                when (event) {

                    is NoteDetailEvents.NavigateToHomeScreen -> {
                        navController.popBackStack()
                    }

                    is NoteDetailEvents.ShowToast -> {
                        toastMessage = ToastUI(
                            message = event.messageRes,
                            type = event.type
                        )
                    }

                    NoteDetailEvents.OpenReminderPicker -> {
                        showDateTimePicker(context) { dateMillis, hour, minute ->
                            viewModel.setReminder(dateMillis, hour, minute)
                        }
                    }

                    NoteDetailEvents.RequestDeleteConfirm -> {
                        activeDialog = AppDialogSpec(
                            type = DialogType.ERROR,
                            messageRes = R.string.delete_note_question,
                            confirmTextRes = R.string.delete,
                            dismissTextRes = R.string.dialog_dismiss_btn,
                            onConfirm = {
                                viewModel.confirmDelete()
                                activeDialog = null
                            },
                            onDismiss = {
                                activeDialog = null
                            }
                        )
                    }

                    NoteDetailEvents.RequestImagePermission -> {
                        activeDialog = AppDialogSpec(
                            type = DialogType.PERMISSION,
                            messageRes = R.string.permission_required_message,
                            confirmTextRes = R.string.go_to_settings,
                            dismissTextRes = R.string.no,
                            onConfirm = {
                                openAppSettingsInDevice(context)
                                activeDialog = null
                            },
                            onDismiss = {
                                activeDialog = null
                            }
                        )
                    }
                }
            }
        }
    }


//    // Back/gesture handling: close sheet first, else delegate to VM
//    BackHandler(enabled = true) {
//        scope.launch {
//            when {
//                showReminderSheet -> {
//                    reminderSheetState.hide()
//                    showReminderSheet = false
//                }
//
//                showTagSheet -> {
//                    tagSheetState.hide()
//                    showTagSheet = false
//                }
//
//                else -> viewModel.backClicked()
//            }
//        }
//    }

    PickImageLauncher(context, onImagePicked = {uri -> viewModel.onImagePicked(uri) })

    activeDialog?.let { dialog ->
        CustomAlertDialog(spec = dialog)
    }

    toastMessage?.let { t->
        ToastHost(
            toast = ToastUI(
                message = t.message, type = t.type
            ), onDismiss = { toastMessage = null }, modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.toast_content_h_padding),
                    vertical = dimensionResource(R.dimen.list_items_v_padding)
                )
        )
    }

//    if (permissionError != null) {
//        CustomAlertDialog(
//            type = DialogType.ERROR,
//            onDismissRequest = { permissionError = null },
//            message = stringResource(permissionError!!),
//            onConfirmBtnClick = {
//                permissionError = null
//                openAppSettingsInDevice(context)
//            },
//            confirmBtnText = R.string.go_to_settings,
//            dismissBtnText = R.string.no,
//            onDismissButtonClick = {},
//            showTopBar = true
//        )
//    }

//    if (showTagSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showTagSheet = false },
//            sheetState = tagSheetState,
//            dragHandle = { BottomSheetDefaults.DragHandle() },
//            containerColor = MaterialTheme.colorScheme.background
//        ) {
//            TagSheetContent(tags = viewModel.tags.collectAsState().value, onSelect = { tag ->
//                viewModel.setTag(tag)
//                showTagSheet = false
//            }, onAdd = { name, color ->
//                viewModel.addTag(name, color)
//                showTagSheet = false
//            })
//        }
//    }

//    if (showDeleteDialog) {
//        CustomAlertDialog(
//            type = DialogType.ERROR,
//            onDismissRequest = { showDeleteDialog = false },
//            message = stringResource(R.string.delete_note_question),
//            onConfirmBtnClick = {
//                showDeleteDialog = false
//                viewModel.confirmDelete()
//            },
//            confirmBtnText = R.string.delete,
//            onDismissButtonClick = { showDeleteDialog = false },
//            dismissBtnText = R.string.dialog_dismiss_btn,
//            showTopBar = true
//        )
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Yellow),
        contentAlignment = Alignment.BottomCenter
    ) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.screen_padding)),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                NDTopBar(
                    editMode = uiState.editMode,
                    onChangeEditMode = { newState -> viewModel.changeEditMode(newState) },
                    saveNote = { viewModel.saveNote({}) })
                Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))
//                Column {
//                    NoteDetailScreenTopBar(
//                        onBack = { viewModel.backClicked() },
//                        onDoneClicked = { viewModel.backClicked() },
//                        onNotificationClick = { showReminderSheet = true },
//                        onShareClick = {},
//                        onDeleteClicked = { viewModel.deleteNote() })
//                    HorizontalDivider(
//                        modifier = Modifier.padding(top = dimensionResource(R.dimen.screen_padding)),
//                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
//                    )
//                }
            },
//            bottomBar = {
//                Box(
//                    Modifier
//                        .fillMaxWidth()
//                        .height(dimensionResource(R.dimen.btm_bar_height))
//                        .animateContentSize()
//                        .background(MaterialTheme.colorScheme.background)
//                ) {
//                    HorizontalDivider(
//                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
//                    )
//
//                    Column(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(
//                                vertical = dimensionResource(R.dimen.btm_sheet_v_padding),
//                                horizontal = dimensionResource(R.dimen.btm_sheet_h_padding)
//                            ), verticalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Row(
//                            Modifier
//                                .fillMaxWidth()
//                                .padding(bottom = dimensionResource(R.dimen.screen_padding)),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Text(
//                                stringResource(R.string.choose_tag),
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//
//                            CircularIconButton(onClick = { editMode = !editMode }, icon = {
//                                Icon(
//                                    ImageVector.vectorResource(R.drawable.ic_edit),
//                                    contentDescription = "Edit"
//                                )
//                            })
//                        }
//
//                        TagsList(
//                            labels = viewModel.tags.collectAsState().value,
//                            horizontalGap = dimensionResource(R.dimen.list_items_h_padding),
//                            onLabelClick = { tag -> viewModel.setTag(tag) },
//                            trailingIcon = ImageVector.vectorResource(R.drawable.ic_add),
//                            onTrailingClick = { showTagSheet = true },
//                            selectedTagId = uiState.note?.tag?.id,
//                            editMode = editMode,
//                            onDeleteClick = { tag -> tagToDelete = tag })
//
//                        if (tagToDelete != null) {
//                            CustomAlertDialog(
//                                type = DialogType.ERROR,
//                                onDismissRequest = { tagToDelete = null },
//                                title = stringResource(R.string.delete_tag),
//                                message = stringResource(
//                                    R.string.delete_tag_confirm, tagToDelete!!.name
//                                ),
//                                onConfirmBtnClick = {
//                                    viewModel.deleteTag(tagToDelete!!.id)
//                                    tagToDelete = null
//                                },
//                                confirmBtnText = R.string.delete,
//                                onDismissButtonClick = { tagToDelete = null },
//                                dismissBtnText = R.string.no,
//                                showTopBar = true
//                            )
//                        }
//                    }
//                }
//            }
        ) { inner ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.BottomCenter
            ) {

                Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                    Text(
                        stringResource(R.string.created_at).plus(" ").plus(stringResource(R.string.colon)).plus(
                            formatUnixMillis(uiState.note?.createdAt ?: System.currentTimeMillis())
                        ), style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))

//                    TagsSection(tag = uiState.note?.tag, reminderTag = uiState.note?.reminderTag)

                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space_max)))

                    TitleSection(
                        title = uiState.note?.title.orEmpty(),
                        onTitleChange = viewModel::updateTitle,
                        placeholder = stringResource(R.string.note_title),
                        editMode = uiState.editMode
                    )

                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space_mid)))

                    BodySection(
                        blocks = uiState.note?.blocks ?: emptyList(),
                        onTextChange = { blockId, text ->
                            viewModel.updateTextBlock(
                                blockId, text
                            )
                        },
                        onDeleteBlock = { blockId -> viewModel.deleteBlock(blockId) },
                        onMoveBlock = { from, to -> viewModel.moveBlock(from, to) },
                        modifier = Modifier,
                        editMode = uiState.editMode
                    )

                    if (uiState.editMode) {
                        NDExpandableIconStack(
                            modifier = Modifier.fillMaxWidth(), icons = listOf(
                                ImageVector.vectorResource(R.drawable.ic_circle_add),    // top icon
                                ImageVector.vectorResource(R.drawable.ic_edit),          // text
                                ImageVector.vectorResource(R.drawable.ic_image),
                                ImageVector.vectorResource(R.drawable.ic_video),
                                ImageVector.vectorResource(R.drawable.ic_mic),
                            ), contentDescriptions = listOf(
                                stringResource(R.string.more),
                                stringResource(R.string.text),
                                stringResource(R.string.image),
                                stringResource(R.string.video),
                                stringResource(R.string.voice)
                            ), onIconClick = { index ->
                                when (index) {
                                    1 -> {
                                        viewModel.addTextBlockAtEnd()
                                    }

                                    2 -> {
                                        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }

                                    3 -> {
                                        // TODO: video
                                    }

                                    4 -> {
                                        // TODO: audio
                                    }
                                }
                            })
                    }
                }

                if (uiState.editMode) EditToolsSection()
            }


//            Column(
//                Modifier
//                    .padding(inner)
//                    .fillMaxSize()
//            ) {
//                when {
//                    uiState.isLoading -> Box(
//                        Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//
//                    uiState.note != null -> NoteContent(
//                        note = uiState.note!!,
//                        onTitleChange = viewModel::updateTitle,
//                        onDescriptionChange = viewModel::updateDescription,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f)
//                    )
//
//                    else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text(stringResource(R.string.note_init_failure))
//                    }
//                }
//            }
        }
    }

//    if (showReminderSheet) {
//        ModalBottomSheet(
//            onDismissRequest = { showReminderSheet = false },
//            sheetState = reminderSheetState,
//            dragHandle = { BottomSheetDefaults.DragHandle() },
//            containerColor = MaterialTheme.colorScheme.background
//        ) {
////            ReminderSheetContent(
////                reminderText = uiState.note?.reminderTag?.name,
////                onClearReminder = { viewModel.clearReminder() },
////                onPickDateTime = {
////                    scope.launch {
////                        val ok = ensureReminderPermissions(requester)
////                        if (!ok) {
////                            permissionError = R.string.permission_required_message
////                            return@launch
////                        }
////                        showReminderSheet = false
////                        viewModel.openReminderPicker()
////                    }
////                })
//        }
//    }
}

@Composable
private fun EditToolsSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.btm_bar_height) / 2)
            .padding(
                vertical = dimensionResource(R.dimen.v_space_min),
                horizontal = dimensionResource(R.dimen.h_space_min)
            ),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = .2f),
        tonalElevation = dimensionResource(R.dimen.ic_button_tonal_elevation),
        shadowElevation = dimensionResource(R.dimen.ic_button_shadow_elevation)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.h_space_min) * 2)
                .horizontalScroll(rememberScrollState())
        ) {

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_tag),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))


            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_clock),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_ordered_list),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_unordered_list),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_underlined),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_italic),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_bold),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )


            Spacer(Modifier.width(dimensionResource(R.dimen.h_space_min)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_align_left),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )
        }
    }
}

@Composable
private fun TitleSection(
    title: String, onTitleChange: (String) -> Unit, placeholder: String, editMode: Boolean
) {
    if (!editMode) {
        Text(
            text = title.ifBlank { placeholder }, style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ), color = if (title.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }, modifier = Modifier.fillMaxWidth()
        )
    } else {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
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
    }
}

@Composable
private fun BodySection(
    blocks: List<NoteBlockUiModel>,
    onTextChange: (blockId: Long, newText: String) -> Unit,
    onDeleteBlock: (blockId: Long) -> Unit,
    onMoveBlock: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    editMode: Boolean
) {
    val scroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
    ) {
        blocks.filter { it.deletedAt == null }.sortedBy { it.position }
            .forEachIndexed { index, block ->
                when (block) {
                    is NoteBlockUiModel.Text -> {
                        NDTextBlockItem(
                            block = block, onTextChange = { newText ->
                            onTextChange(block.id, newText)
                        }, onDelete = { onDeleteBlock(block.id) }, editMode = editMode
                        )
                    }

                    is NoteBlockUiModel.Media -> {
                        NDMediaBlockItem(
                            block = block,
                            onDelete = { onDeleteBlock(block.id) },
                            editMode = editMode
                        )
                    }
                }

                Spacer(Modifier.height(dimensionResource(R.dimen.v_space_min)))
            }
    }
}