package com.app.noteapp.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.noteapp.R
import com.app.noteapp.domain.model.Tag
import com.app.noteapp.presentation.components.DateRangePickerDialog
import com.app.noteapp.presentation.components.TagsList
import com.app.noteapp.presentation.screens.home.TimeFilter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotesFilterSheet(
    tags: List<Tag>,
    selectedTagId: Long?,
    onTagSelected: (Tag) -> Unit,
    timeFilter: TimeFilter,
    onTimeFilterSelected: (TimeFilter) -> Unit,
    rangeStart: Long?,
    rangeEnd: Long?,
    onCustomRangeSelected: (Long?, Long?) -> Unit,
    onlyReminder: Boolean,
    onOnlyReminderChange: (Boolean) -> Unit,
    onClose: () -> Unit,
) {
    var showRangePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.filter_notes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(modifier = Modifier
                .clickable { onClose() }
                .padding(vertical = 8.dp),
                text = stringResource(R.string.close),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(42.dp))

        // 1. Tags section
        Text(
            text = stringResource(R.string.filter_by_tag),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        TagsList(
            labels = tags,
            selectedTagId = selectedTagId,
            cornerRadius = 18.dp,
            horizontalGap = 12.dp,
            verticalGap = 12.dp,
            onLabelClick = { tag -> onTagSelected(tag) })

        Spacer(Modifier.height(42.dp))

        // 2. Time filter section
        Text(
            text = stringResource(R.string.filter_by_time),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showRangePicker = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(R.string.pick_date_range),
                    style = MaterialTheme.typography.bodyMedium
                )
                val rangeText = remember(rangeStart, rangeEnd) {
                    if (rangeStart != null && rangeEnd != null) formatRange(
                        rangeStart, rangeEnd
                    )
                    else ""
                }
                if (rangeText.isNotEmpty() || rangeText.isNotBlank()) {
                    Text(
                        text = rangeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                contentDescription = null
            )
        }

        if (showRangePicker) {
            DateRangePickerDialog(
                initialStart = rangeStart,
                initialEnd = rangeEnd,
                onDismiss = { showRangePicker = false },
                onConfirm = { start, end ->
                    showRangePicker = false
                    if (start != null && end != null) {
                        onCustomRangeSelected(start, end)
                    }
                })
        }

        Spacer(Modifier.height(18.dp))

        // 3. Reminders filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.filter_by_reminder_desc),
                style = MaterialTheme.typography.bodyMedium,
            )

            androidx.compose.material3.Switch(
                colors = SwitchDefaults.colors(
                    checkedIconColor = MaterialTheme.colorScheme.primary,
                    uncheckedIconColor = MaterialTheme.colorScheme.secondary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                    uncheckedBorderColor = MaterialTheme.colorScheme.secondary
                ), checked = onlyReminder, onCheckedChange = onOnlyReminderChange
            )
        }
    }
}

fun formatRange(
    startMillis: Long, endMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()
): String {
    val startDate = Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDate()
    val endDate = Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDate()

    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    return if (startDate == endDate) {
        formatter.format(startDate)
    } else {
        "${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }
}