package com.app.noteapp.core.extensions

import java.text.NumberFormat
import java.util.Locale

fun Int.toLocalizedDigits(locale: Locale): String =
    NumberFormat.getInstance(locale).format(this)