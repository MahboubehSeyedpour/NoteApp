package com.app.noteapp.core.extensions

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.IsoFields

fun Long.isToday(zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
    val date = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
    val today = LocalDate.now(zoneId)
    return date == today
}

fun Long.isThisWeek(zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
    val date = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
    val now = LocalDate.now(zoneId)

    val weekField = IsoFields.WEEK_OF_WEEK_BASED_YEAR
    val weekOfYearDate = date.get(weekField)
    val weekOfYearNow = now.get(weekField)

    val yearDate = date.get(IsoFields.WEEK_BASED_YEAR)
    val yearNow = now.get(IsoFields.WEEK_BASED_YEAR)

    return yearDate == yearNow && weekOfYearDate == weekOfYearNow
}

fun Long.isThisMonth(zoneId: ZoneId = ZoneId.systemDefault()): Boolean {
    val date = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
    val now = LocalDate.now(zoneId)
    return date.year == now.year && date.month == now.month
}