package com.app.noteapp.presentation.components

import android.R.attr.verticalGap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import com.app.noteapp.R
import com.app.noteapp.domain.model.Tag
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotesFilterSheet(
    tags: List<Tag>,
    selectedTagId: Long?,
    onFilterClicked: (Long?, Long?, Long?, Boolean) -> Unit,
    onDeleteAllFiltersClicked: () -> Unit,
    rangeStart: Long?,
    rangeEnd: Long?,
    onCustomRangeSelected: (Long?, Long?) -> Unit,
    onlyReminder: Boolean,
    onOnlyReminderChange: (Boolean) -> Unit,
    onClose: () -> Unit,
) {
    var showRangePicker by remember { mutableStateOf(false) }
    var selectedTagId by remember { mutableStateOf(selectedTagId) }

    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(
                horizontal = dimensionResource(R.dimen.btm_sheet_h_padding),
                vertical = dimensionResource(R.dimen.btm_sheet_v_padding)
            )
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

            Text(
                modifier = Modifier.clickable { onClose() },
                text = stringResource(R.string.close),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.btm_sheet_section_padding)))

        // 1. Tags section
        Text(
            text = stringResource(R.string.filter_by_tag),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.btm_sheet_inter_section_padding)))

        TagsList(
            labels = tags,
            selectedTagId = selectedTagId,
            horizontalGap = dimensionResource(R.dimen.list_items_h_padding),
            verticalGap = dimensionResource(R.dimen.list_items_v_padding),
            onLabelClick = { tag -> selectedTagId = tag.id })

        Spacer(Modifier.height(dimensionResource(R.dimen.btm_sheet_section_padding)))

        // 2. Time filter section
        Text(
            text = stringResource(R.string.filter_by_time),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.btm_sheet_inter_section_padding)))

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
                    if (rangeStart != null && rangeEnd != null) formatRange(rangeStart, rangeEnd)
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

        Spacer(Modifier.height(dimensionResource(R.dimen.btm_sheet_inter_section_padding)))

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

            Switch(
                colors = SwitchDefaults.colors(
                    checkedIconColor = MaterialTheme.colorScheme.primary,
                    uncheckedIconColor = MaterialTheme.colorScheme.secondary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                    uncheckedBorderColor = MaterialTheme.colorScheme.secondary
                ), checked = onlyReminder, onCheckedChange = onOnlyReminderChange
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.btm_sheet_section_padding)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NoteAppButton(
                modifier = Modifier.weight(1f),
                R.string.filter,
                onClick = { onFilterClicked(selectedTagId, rangeStart, rangeEnd, onlyReminder) })

            Spacer(Modifier.weight(.2f))

            NoteAppButton(
                modifier = Modifier.weight(1f),
                R.string.delete_all_filters,
                onClick = onDeleteAllFiltersClicked
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