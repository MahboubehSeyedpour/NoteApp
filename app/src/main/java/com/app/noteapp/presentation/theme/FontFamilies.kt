package com.app.noteapp.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.app.noteapp.R
import com.app.noteapp.domain.model.preferences_model.FontPref

private val IranNastaliqFontFamily = FontFamily(
    Font(R.font.iran_nastaliq),
)

private val IranSansFontFamily = FontFamily(
    Font(R.font.iran_sans_edit),
    Font(R.font.iran_sans_bold_edit),
)

private val PelakFontFamily = FontFamily(
    Font(R.font.pelak_bold),
    Font(R.font.pelak_thin),
    Font(R.font.pelak_medium),
    Font(R.font.pelak_regular),
    Font(R.font.pelak_black),
    Font(R.font.pelak_extra_bold),
    Font(R.font.pelak_light),
    Font(R.font.pelak_no_eng_bold),
    Font(R.font.pelak_no_eng_extrabold),
    Font(R.font.pelak_no_eng_light),
    Font(R.font.pelak_no_eng_medium),
    Font(R.font.pelak_no_eng_regular),
    Font(R.font.pelak_no_eng_semibold),
    Font(R.font.pelak_no_eng_thin),
    Font(R.font.pelak_semi_bold),
)

private val ShabnamFontFamily = FontFamily(
    Font(R.font.shabnam),
    Font(R.font.shabnam_medium),
    Font(R.font.shabnam_bold),
    Font(R.font.shabnam_thin),
    Font(R.font.shabnam_light),
)

private val BoldingFontFamily = FontFamily(
    Font(R.font.bolding),
)

private val SchoolPlannerFontFamily = FontFamily(
    Font(R.font.school_planner),
)

private val BromlisFontFamily = FontFamily(
    Font(R.font.bromlis_regular),
)

fun fontFamilyFor(appFont: FontPref): FontFamily =
    when (appFont) {
        FontPref.IRAN_NASTALIQ -> IranNastaliqFontFamily
        FontPref.IRAN_SANS -> IranSansFontFamily
        FontPref.PELAK -> PelakFontFamily
        FontPref.SHABNAM -> ShabnamFontFamily
        FontPref.BOLDING -> BoldingFontFamily
        FontPref.SCHOOL_PLANNER -> SchoolPlannerFontFamily
        FontPref.BROMLIS_REGULAR -> BromlisFontFamily
    }