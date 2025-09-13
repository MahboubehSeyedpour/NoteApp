package com.example.noteapp.core.time

import java.time.Instant
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