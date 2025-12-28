package com.app.noteapp.data.utils

/**
 * Helper: if timestamp <= 0, replace with now.
 */
fun normalizeTs(value: Long, now: Long): Long =
    if (value <= 0L) now else value
