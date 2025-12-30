package com.app.noteapp.domain.model.preferences_model

import androidx.annotation.StringRes
import com.app.noteapp.R

enum class FontPref(
    val key: String,
    val supportedLanguages: Set<LanguagePref>,
    @StringRes val labelResId: Int
) {
    // -------- Persian UI fonts --------
    IRAN_NASTALIQ(
        key = "iran_nastaliq",
        supportedLanguages = setOf(LanguagePref.FA),
        labelResId = R.string.font_iran_nastaliq
    ),
    IRAN_SANS(
        key = "iran_sans",
        supportedLanguages = setOf(LanguagePref.FA),
        labelResId = R.string.font_iran_sans
    ),
    PELAK(
        key = "pelak",
        supportedLanguages = setOf(LanguagePref.FA),
        labelResId = R.string.font_pelak
    ),
    SHABNAM(
        key = "shabnam",
        supportedLanguages = setOf(LanguagePref.FA),
        labelResId = R.string.font_shabnam
    ),

    // -------- English UI fonts --------
    BOLDING(
        key = "bolding",
        supportedLanguages = setOf(LanguagePref.EN),
        labelResId = R.string.font_bolding
    ),
    BROMLIS_REGULAR(
        key = "bromlis_regular",
        supportedLanguages = setOf(LanguagePref.EN),
        labelResId = R.string.font_bromlis
    ),
    SCHOOL_PLANNER(
        key = "school_planner",
        supportedLanguages = setOf(LanguagePref.EN),
        labelResId = R.string.font_school_planner
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

