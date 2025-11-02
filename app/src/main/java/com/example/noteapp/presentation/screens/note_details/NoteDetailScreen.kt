package com.example.noteapp.presentation.screens.note_details

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.noteapp.R
import com.example.noteapp.presentation.components.CustomBottomBar
import com.example.noteapp.presentation.components.CustomBottomSheet
import com.example.noteapp.presentation.components.CustomReminderDialog
import com.example.noteapp.presentation.components.NoteContent
import com.example.noteapp.presentation.components.NoteDetailScreenTopBar
import com.example.noteapp.presentation.components.ReminderEntryPoint
import com.example.noteapp.presentation.components.ReminderPickerDialog
import com.example.noteapp.presentation.screens.BottomSheetRowModel
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
        rows = listOf(
            BottomSheetRowModel(
                icon = R.drawable.ic_clock,
                title = R.string.btm_sheet_later_today,
                value = { Text(context.getString(R.string.btm_sheet_6_pm)) },
                onRowClicked = {}
            ),
            BottomSheetRowModel(
                icon = R.drawable.ic_clock,
                title = R.string.btm_sheet_tomorrow_morning,
                value = { Text(context.getString(R.string.btm_sheet_6_pm)) },
                onRowClicked = {}
            ),
            BottomSheetRowModel(
                icon = R.drawable.ic_location,
                title = R.string.btm_sheet_home,
                value = { Text(context.getString(R.string.btm_sheet_Tehran)) },
                onRowClicked = {}
            ),
            BottomSheetRowModel(
                icon = R.drawable.ic_calendar,
                title = R.string.btm_sheet_pick_date,
                value = {
                    IconButton(onClick = {
                        showSheet = false
                        showCustomDialog = true
                    }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add")
                    }
                },
                onRowClicked = {
                    showSheet = false
                    showCustomDialog = true
                }
            ),
        )
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