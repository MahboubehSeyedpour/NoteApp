package com.app.noteapp.domain.model

enum class AppFont(val key: String) {
    IRAN_NASTALIQ("iran_nastaliq"),
    IRAN_SANS("iran_sans"),
    PELAK("pelak"),
    SHABNAM("shabnam");

    companion object {
        fun fromKey(key: String?): AppFont =
            entries.firstOrNull { it.key == key } ?: PELAK
    }
}