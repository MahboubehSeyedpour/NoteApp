package com.app.noteapp.domain.model.preferences_model

enum class FontPref(
    val key: String,
    val supportedLanguages: Set<LanguagePref>
) {
    // -------- Persian UI fonts --------
    IRAN_NASTALIQ(
        key = "iran_nastaliq",
        supportedLanguages = setOf(LanguagePref.FA)
    ),
    IRAN_SANS(
        key = "iran_sans",
        supportedLanguages = setOf(LanguagePref.FA)
    ),
    PELAK(
        key = "pelak",
        supportedLanguages = setOf(LanguagePref.FA)
    ),
    SHABNAM(
        key = "shabnam",
        supportedLanguages = setOf(LanguagePref.FA)
    ),

    // -------- English UI fonts --------
    BOLDING(
        key = "bolding",
        supportedLanguages = setOf(LanguagePref.EN)
    ),
    BROMLIS_REGULAR(
        key = "bromlis_regular",
        supportedLanguages = setOf(LanguagePref.EN)
    ),
    SCHOOL_PLANNER(
        key = "school_planner",
        supportedLanguages = setOf(LanguagePref.EN)
    );

    companion object {

        fun defaultFor(language: LanguagePref): FontPref =
            when (language) {
                LanguagePref.FA -> SHABNAM
                LanguagePref.EN -> BROMLIS_REGULAR
            }

        fun fromKey(key: String?, language: LanguagePref): FontPref {
            if (key == null) return defaultFor(language)

            val found = entries.firstOrNull { it.key == key }

            return if (found != null && language in found.supportedLanguages) {
                found
            } else {
                defaultFor(language)
            }
        }

        fun availableFor(language: LanguagePref): List<FontPref> =
            entries.filter { language in it.supportedLanguages }
    }
}

fun FontPref.supports(language: LanguagePref): Boolean =
    language in supportedLanguages

