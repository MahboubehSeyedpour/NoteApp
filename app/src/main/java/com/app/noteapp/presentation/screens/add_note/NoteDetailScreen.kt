package com.app.noteapp.presentation.screens.add_note

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.noteapp.R
import com.app.noteapp.core.permissions.PermissionRequest
import com.app.noteapp.core.permissions.PermissionResult
import com.app.noteapp.core.permissions.awaitPermission
import com.app.noteapp.core.permissions.rememberPermissionRequester
import com.app.noteapp.core.time.formatUnixMillis
import com.app.noteapp.data.local.model.enums.MediaKind
import com.app.noteapp.presentation.components.CircularIconButton
import com.app.noteapp.presentation.components.CustomAlertDialog
import com.app.noteapp.presentation.components.NoteTag
import com.app.noteapp.presentation.components.TagsList
import com.app.noteapp.presentation.components.ToastHost
import com.app.noteapp.presentation.components.showDateTimePicker
import com.app.noteapp.presentation.model.DialogType
import com.app.noteapp.presentation.model.NoteBlockUiModel
import com.app.noteapp.presentation.model.TagUiModel
import com.app.noteapp.presentation.model.ToastUI
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
    var showReminderSheet by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }
    val reminderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tagSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    val requester = rememberPermissionRequester()
    var permissionError by remember { mutableStateOf<Int?>(null) }
    var toast by remember { mutableStateOf<ToastUI?>(null) }

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

    suspend fun ensureImagePermissions(
        requester: (PermissionRequest, (PermissionResult) -> Unit) -> Unit
    ): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> true

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                awaitPermission(
                    requester,
                    PermissionRequest.Runtime(Manifest.permission.READ_MEDIA_IMAGES)
                ) == PermissionResult.GRANTED
            }

            else -> {
                awaitPermission(
                    requester,
                    PermissionRequest.Runtime(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) == PermissionResult.GRANTED
            }
        }
    }


    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { e ->
                when (e) {
                    is NoteDetailEvents.NavigateToHomeScreen -> navController.popBackStack()
                    is NoteDetailEvents.ShowToast -> {
                        toast = ToastUI(
                            message = e.messageRes, type = e.type
                        )
                    }

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

                else -> viewModel.backClicked()
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImagePicked(uri)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        TextButton(onClick = {}) {
                            Text(stringResource(R.string.save))
                        }
                        CircularIconButton(onClick = {}, icon = {
                            Icon(
                                ImageVector.vectorResource(R.drawable.ic_redo),
                                contentDescription = "Delete"
                            )
                        })
                        CircularIconButton(
                            onClick = {},
                            icon = {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.ic_undo),
                                    contentDescription = "Notify"
                                )
                            },
                        )
                    }

                    CircularIconButton(onClick = {}, icon = {
                        Icon(
                            ImageVector.vectorResource(R.drawable.ic_arrow_left),
                            contentDescription = "Back"
                        )
                    })
                }
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
                    CreationDateSection(uiState.note?.createdAt ?: System.currentTimeMillis())

                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

//                    TagsSection(tag = uiState.note?.tag, reminderTag = uiState.note?.reminderTag)

                    Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

                    TitleSection(
                        title = uiState.note?.title ?: "",
                        onTitleChange = { title -> viewModel.updateTitle(title) },
                        placeholder = stringResource(R.string.note_title)
                    )

                    BodySection(
                        blocks = uiState.note?.blocks ?: listOf(),
                        modifier = Modifier,
                    )

                    ExpandableIconStack(
                        modifier = Modifier.fillMaxWidth(),
                        icons = listOf(
                            ImageVector.vectorResource(R.drawable.ic_circle_add),    // top icon
                            ImageVector.vectorResource(R.drawable.ic_edit),
                            ImageVector.vectorResource(R.drawable.ic_image),
                            ImageVector.vectorResource(R.drawable.ic_video),
                            ImageVector.vectorResource(R.drawable.ic_mic),
                        ),
                        contentDescriptions = listOf(
                            stringResource(R.string.more),
                            stringResource(R.string.text),
                            stringResource(R.string.image),
                            stringResource(R.string.video),
                            stringResource(R.string.voice)
                        ),
                        onIconClick = { index ->
                            when (index) {
                                1 -> {}
                                2 -> {
                                    scope.launch {
                                        val ok = ensureImagePermissions(requester)
                                        if (!ok) {
                                            // همین قبلاً هم برای reminder ازش استفاده می‌کردی
                                            permissionError = R.string.permission_required_message
                                            return@launch
                                        }

                                        imagePickerLauncher.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    }
                                }
                                3 -> {}
                                4 -> {}
                            }
                        })
                }

                EditSection()
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

        toast?.let { t ->
            ToastHost(
                toast = ToastUI(
                    message = t.message, type = t.type
                ), onDismiss = { toast = null }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.toast_content_h_padding),
                        vertical = dimensionResource(R.dimen.list_items_v_padding)
                    )
            )
        }
    }

    if (showReminderSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReminderSheet = false },
            sheetState = reminderSheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.background
        ) {
//            ReminderSheetContent(
//                reminderText = uiState.note?.reminderTag?.name,
//                onClearReminder = { viewModel.clearReminder() },
//                onPickDateTime = {
//                    scope.launch {
//                        val ok = ensureReminderPermissions(requester)
//                        if (!ok) {
//                            permissionError = R.string.permission_required_message
//                            return@launch
//                        }
//                        showReminderSheet = false
//                        viewModel.openReminderPicker()
//                    }
//                })
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
                viewModel.setTag(tag)
                showTagSheet = false
            }, onAdd = { name, color ->
                viewModel.addTag(name, color)
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
                viewModel.confirmDelete()
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
            .padding(
                horizontal = dimensionResource(R.dimen.btm_sheet_h_padding),
                vertical = dimensionResource(R.dimen.list_items_v_padding)
            )
    ) {
        Text(stringResource(R.string.add_reminder), style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

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

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
    }
}


@Composable
fun TagSheetContent(
    tags: List<TagUiModel>, onSelect: (TagUiModel) -> Unit, onAdd: (String, Color) -> Unit
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
            .padding(
                horizontal = dimensionResource(R.dimen.btm_sheet_h_padding),
                vertical = dimensionResource(R.dimen.btm_sheet_v_padding)
            )
    ) {
        Text(stringResource(R.string.tags), style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        TagsList(
            labels = tags, onLabelClick = onSelect, trailingIcon = null
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            placeholder = { Text(stringResource(R.string.tag_name)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .border(dimensionResource(R.dimen.dp_1), MaterialTheme.colorScheme.primary),
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            preset.forEach { c ->
                Box(
                    Modifier
                        .size(
                            if (c == selectedColor) dimensionResource(R.dimen.tag_size_on_select) else dimensionResource(
                                R.dimen.tag_size_normal
                            )
                        )
                        .clip(CircleShape)
                        .background(c)
                        .border(
                            width = if (c == selectedColor) dimensionResource(R.dimen.dp_6) else dimensionResource(
                                R.dimen.dp_1
                            ),
                            color = if (c == selectedColor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = c }, contentAlignment = Alignment.Center
                ) {}
            }
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))

//        NoteAppButton(
//            modifier = Modifier.fillMaxWidth(),
//            text = R.string.add_tag,
//            onClick = { if (newName.isNotBlank()) onAdd(newName.trim(), selectedColor) })
//
//        Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
    }
}

fun generatePaletteFromSeed(
    seed: Color, count: Int = 24
): List<Color> {
    // Generate hues around the seed hue
    val seedHsl = FloatArray(3)
    ColorUtils.colorToHSL(seed.toArgb(), seedHsl)
    val seedHue = seedHsl[0]

    val colors = mutableListOf<Color>()
    val step = 360f / count

    for (i in 0 until count) {
        val h = (seedHue + i * step) % 360f
        val s = 0.65f
        val l = 0.55f
        val argb = ColorUtils.HSLToColor(floatArrayOf(h, s, l))
        colors += Color(argb)
    }

    return colors
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorPalettePicker(
    colors: List<Color>,
    selected: Color,
    onSelect: (Color) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 8
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 6.dp)
    ) {
        items(items = colors, key = { it.value.toString() }) { c ->
            val selectedNow = c == selected
            Box(
                modifier = Modifier
                    .size(if (selectedNow) 34.dp else 28.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(
                        width = if (selectedNow) 4.dp else 1.dp,
                        color = if (selectedNow) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { onSelect(c) })
        }
    }
}

@Composable
fun EditSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.btm_bar_height) / 2)
            .padding(
                vertical = dimensionResource(R.dimen.v_space),
                horizontal = dimensionResource(R.dimen.h_space)
            ),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = .2f),
        tonalElevation = dimensionResource(R.dimen.ic_button_tonal_elevation),
        shadowElevation = dimensionResource(R.dimen.ic_button_shadow_elevation)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.h_space) * 2)
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

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))


            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_clock),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_ordered_list),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_unordered_list),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_underlined),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_italic),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )

            Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))

            CircularIconButton(
                onClick = {},
                icon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_text_bold),
                        contentDescription = stringResource(R.string.text_bold)
                    )
                },
            )


            Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))

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
fun CreationDateSection(unixTimeInMillis: Long) {
    Text(
        stringResource(R.string.created_at).plus(" ").plus(stringResource(R.string.colon)).plus(
            formatUnixMillis(unixTimeInMillis)
        ), style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun TagsSection(tag: TagUiModel?, reminderTag: TagUiModel?) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.list_items_h_padding)),
        verticalArrangement = Arrangement.Center
    ) {
        tag?.let {
            NoteTag(it, icon = R.drawable.ic_hashtag)
        }

        reminderTag?.let {
            NoteTag(it, icon = R.drawable.ic_clock)
        }
    }
}

@Composable
fun TitleSection(title: String, onTitleChange: (String) -> Unit, placeholder: String) {
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

@Composable
fun BodySection(
    blocks: List<NoteBlockUiModel>,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(bottom = dimensionResource(R.dimen.btm_bar_height) / 2)
    ) {
        blocks
            .filter { it.deletedAt == null }
            .sortedBy { it.position }
            .forEach { block ->
                when (block) {
                    is NoteBlockUiModel.Text -> TextBlockItem(block)
                    is NoteBlockUiModel.Media -> MediaBlockItem(block)
                }

                Spacer(Modifier.height(dimensionResource(R.dimen.v_space)))
            }
    }
}

@Composable
private fun TextBlockItem(
    block: NoteBlockUiModel.Text
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.icon_size)),
        text = block.text,
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Normal,
        ),
        textAlign = TextAlign.Start
    )
}

@Composable
private fun MediaBlockItem(
    block: NoteBlockUiModel.Media
) {
    val context = LocalContext.current

    when (block.kind) {
        MediaKind.IMAGE -> {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(block.localUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 320.dp)
                    .padding(horizontal = dimensionResource(R.dimen.icon_size))
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        MediaKind.VIDEO -> {
            MediaChip(
                icon = ImageVector.vectorResource(R.drawable.ic_play),
                label = "Video",
                uri = block.localUri,
                durationMs = block.durationMs
            )
        }

        MediaKind.AUDIO -> {
            MediaChip(
                icon = ImageVector.vectorResource(R.drawable.ic_play),
                label = "Audio",
                uri = block.localUri,
                durationMs = block.durationMs
            )
        }
    }
}

@Composable
private fun MediaChip(
    icon: ImageVector,
    label: String,
    uri: String,
    durationMs: Long?,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.icon_size)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.tag_h_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(dimensionResource(R.dimen.h_space)))

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = uri,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(onClick = { /* TODO: play using ExoPlayer */ }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_play),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ExpandableIconStack(
    modifier: Modifier = Modifier,
    icons: List<ImageVector>,
    contentDescriptions: List<String?> = List(icons.size) { null },
    onIconClick: (index: Int) -> Unit,
    iconSize: Dp = 40.dp,
    gap: Dp = 10.dp,
) {
    require(icons.isNotEmpty()) { "icons must not be empty" }
    require(contentDescriptions.size == icons.size) { "contentDescriptions size mismatch" }

    var expanded by rememberSaveable { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded, label = "stack")

    Box(
        modifier = modifier, contentAlignment = Alignment.CenterStart
    ) {
        for (i in icons.indices.reversed()) {

            val offsetX by transition.animateDp(
                label = "offset-$i", transitionSpec = {
                    tween(
                        durationMillis = 260, delayMillis = (i * 35), easing = FastOutSlowInEasing
                    )
                }) { isExpanded ->
                if (!isExpanded) 0.dp else ((iconSize + gap) * i)
            }

            val alpha by transition.animateFloat(
                label = "alpha-$i",
                transitionSpec = { tween(durationMillis = 180) }) { isExpanded ->
                if (!isExpanded && i != 0) 0f else 1f
            }

            IconButton(
                onClick = {
                    if (i == 0) expanded = !expanded
                    else onIconClick(i)
                },
                enabled = (i == 0) || expanded,
                modifier = Modifier
                    .size(iconSize)
                    .offset(x = offsetX)
                    .zIndex((icons.size - i).toFloat())
                    .graphicsLayer { this.alpha = alpha }) {
                Icon(
                    imageVector = icons[i], contentDescription = contentDescriptions[i]
                )
            }
        }
    }
}

