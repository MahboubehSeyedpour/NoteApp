package com.app.noteapp.core.time

import com.app.noteapp.presentation.screens.home.TimeFilter
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val MONTH_DAY_TIME =
    DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH)

fun formatReminderEpoch(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
    val base = Instant.ofEpochMilli(epochMillis).atZone(zoneId).format(MONTH_DAY_TIME)
    val i = base.indexOf(' ')
    return if (i > 0) base.substring(0, i).lowercase(Locale.ENGLISH) + base.substring(i)
    else base.lowercase(Locale.ENGLISH)
}

fun combineToEpochMillis(
    dateMillis: Long,
    hour: Int,
    minute: Int,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long {
    val localDate = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate()
    return ZonedDateTime.of(localDate, LocalTime.of(hour, minute), zoneId)
        .toInstant().toEpochMilli()
}

fun formatDate(millis: Long): String {
    val zoned = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return zoned.format(formatter)
}

fun formatTime(h: Int, m: Int): String {
    val dt = LocalTime.of(h, m)
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return dt.format(formatter)
}

data class TimeRange(val start: Long, val endExclusive: Long)

private fun LocalDate.startOfDayMillis(zoneId: ZoneId): Long =
    this.atStartOfDay(zoneId).toInstant().toEpochMilli()

private fun LocalDate.endExclusiveMillis(zoneId: ZoneId): Long =
    this.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

fun rangeFor(
    filter: TimeFilter,
    startMillis: Long?,
    endMillis: Long?,
    zoneId: ZoneId
): TimeRange? {
    val now = Instant.now().atZone(zoneId)

    return when (filter) {
        TimeFilter.ALL -> {
            // filter is ON but no date restriction
            null
        }

        TimeFilter.TODAY -> {
            val today = now.toLocalDate()
            TimeRange(
                start = today.atStartOfDay(zoneId).toInstant().toEpochMilli(),
                endExclusive = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
            )
        }

        TimeFilter.THIS_WEEK -> {
            val today = now.toLocalDate()
            val weekStart = today.with(DayOfWeek.SATURDAY)
            val weekEnd = weekStart.plusDays(7)

            TimeRange(
                start = weekStart.atStartOfDay(zoneId).toInstant().toEpochMilli(),
                endExclusive = weekEnd.atStartOfDay(zoneId).toInstant().toEpochMilli()
            )
        }

        TimeFilter.THIS_MONTH -> {
            val today = now.toLocalDate()
            val firstDay = today.withDayOfMonth(1)
            val firstNextMonth = firstDay.plusMonths(1)

            TimeRange(
                start = firstDay.atStartOfDay(zoneId).toInstant().toEpochMilli(),
                endExclusive = firstNextMonth.atStartOfDay(zoneId).toInstant().toEpochMilli()
            )
        }

        TimeFilter.CUSTOM_RANGE -> {
            if (startMillis == null || endMillis == null) {
                // Filter ON but user didn’t pick both dates -> treat as no date restriction
                null
            } else {
                // Convert picker millis → LocalDate in system zone
                val startDate = Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDate()
                val endDate = Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDate()

                val normalizedStart = minOf(startDate, endDate)
                val normalizedEnd = maxOf(startDate, endDate)

                TimeRange(
                    start = normalizedStart.atStartOfDay(zoneId).toInstant().toEpochMilli(),
                    endExclusive = normalizedEnd
                        .plusDays(1)
                        .atStartOfDay(zoneId)
                        .toInstant()
                        .toEpochMilli()
                )
            }
        }
    }
}
