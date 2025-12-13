package com.app.noteapp.domain.common_model

enum class AppFont(val key: String) {
    IRAN_NASTALIQ("iran_nastaliq"),
    IRAN_SANS("iran_sans"),
    PELAK("pelak"),
    SHABNAM("shabnam"),
    BOLDING("bolding"),
    BROMLIS_REGULAR("bromlis_regular"),
    SCHOOL_PLANNER("school_planner");

    companion object {
        fun fromKey(key: String?): AppFont =
            entries.firstOrNull { it.key == key } ?: SHABNAM
    }
}