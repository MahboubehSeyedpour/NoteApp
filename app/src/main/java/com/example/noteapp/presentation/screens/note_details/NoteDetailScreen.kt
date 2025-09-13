package com.example.noteapp.presentation.screens.note_details

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.R
import com.example.noteapp.domain.model.Note
import com.example.noteapp.presentation.components.CustomBottomBar
import com.example.noteapp.presentation.components.CustomBottomSheet
import com.example.noteapp.presentation.components.CustomReminderDialog
import com.example.noteapp.presentation.components.NoteDetailScreenTopBar
import com.example.noteapp.presentation.components.ReminderEntryPoint
import com.example.noteapp.presentation.components.ReminderPickerDialog
import com.example.noteapp.presentation.screens.note_details.components.BadgesRow
import com.example.noteapp.presentation.theme.Background
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NoteDetailsScreen(
    navController: NavController,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    var showSheet by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collectLatest { e ->
                when (e) {
                    is NoteDetailEvents.NavigateToHomeScreen -> navController.popBackStack()
                    is NoteDetailEvents.Error ->
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CustomBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = false },
        row1Icon = ImageVector.vectorResource(R.drawable.ic_clock),
        row1Title = context.getString(R.string.btm_sheet_later_today),
        row1Value = context.getString(R.string.btm_sheet_6_pm),
        onRow1Click = { },

        row2Icon = ImageVector.vectorResource(R.drawable.ic_clock),
        row2Title = context.getString(R.string.btm_sheet_tomorrow_morning),
        row2Value = context.getString(R.string.btm_sheet_6_pm),
        onRow2Click = { },

        row3Icon = ImageVector.vectorResource(R.drawable.ic_home),
        row3Title = context.getString(R.string.btm_sheet_home),
        row3Value = context.getString(R.string.btm_sheet_Tehran),
        onRow3Click = { },

        row4Icon = ImageVector.vectorResource(R.drawable.ic_calendar),
        row4Title = context.getString(R.string.btm_sheet_pick_date),
        onRow4PlusClick = {
            showSheet = false
            showCustomDialog = true
        }
    )

    ReminderEntryPoint(openPicker = { showPicker = true })

    if (showPicker) {
        ReminderPickerDialog(
            onDismiss = { showPicker = false },
            onConfirm = { dateMillis, hour, minute ->
                viewModel.setReminder(dateMillis, hour, minute)
                showPicker = false
            }
        )
    }


    CustomReminderDialog(
        visible = showCustomDialog,
        onDismiss = { showCustomDialog = false },
        onConfirm = { dateMillis, hour, minute, repeat ->
            viewModel.setReminder(dateMillis, hour, minute)
            showSheet = false
            showCustomDialog = false
        },
    )

    Scaffold(
        containerColor = Background,
        topBar = {
            NoteDetailScreenTopBar(
                onBack = { viewModel.onBackClicked() },
                onNotificationClick = { showSheet = true },
                onArchiveClick = {}
            )
        },
        bottomBar = {
            CustomBottomBar(
                label = "Labels",
                onLabelsClick = {},
                onFabClick = { viewModel.onDoneClicked() },
                fabIcon = Icons.Default.Done
            )
        }
    ) { inner ->
        when {
            uiState.isLoading -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState.note != null -> NoteContent(
                note = uiState.note!!,
                onTitleChange = viewModel::updateTitle,
                onDescriptionChange = viewModel::updateDescription,
                modifier = Modifier.padding(inner),
            )

            else -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner), contentAlignment = Alignment.Center
            ) {
                Text(context.getString(R.string.note_load_failure))
            }
        }
    }

}

@Composable
fun NoteContent(
    note: Note,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp),
    maxContentWidth: Dp = 480.dp
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(top = 8.dp)
            .then(Modifier.padding(contentPadding)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxContentWidth),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            BadgesRow(
                categoryBadge = note.categoryBadge,
                timeBadge = note.timeBadge,
            )

            OutlinedTextField(
                value = note.title,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                placeholder = { Text(context.getString(R.string.note_title)) },
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

            OutlinedTextField(
                value = note.description.orEmpty(),
                onValueChange = {
                    it.ifBlank { null }?.let { value -> onDescriptionChange(value) }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(context.getString(R.string.note_description)) },
                minLines = 4,
                maxLines = 12,
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
}