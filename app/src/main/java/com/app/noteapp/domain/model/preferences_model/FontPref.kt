package com.app.noteapp.domain.model.preferences_model

enum class FontPref(val key: String) {
    IRAN_NASTALIQ("iran_nastaliq"),
    IRAN_SANS("iran_sans"),
    PELAK("pelak"),
    SHABNAM("shabnam"),
    BOLDING("bolding"),
    BROMLIS_REGULAR("bromlis_regular"),
    SCHOOL_PLANNER("school_planner");

    companion object {
        fun fromKey(key: String?): FontPref =
            entries.firstOrNull { it.key == key } ?: SHABNAM
    }
}
