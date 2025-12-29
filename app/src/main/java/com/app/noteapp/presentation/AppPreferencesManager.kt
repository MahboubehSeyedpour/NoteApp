package com.app.noteapp.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import java.util.Locale

object AppLanguageManager {

    fun applyLanguage(language: LanguagePref) {
        val locale = when (language) {
            LanguagePref.FA -> Locale("fa")
            LanguagePref.EN -> Locale.ENGLISH
        }

        val localeList = LocaleListCompat.create(locale)

        AppCompatDelegate.setApplicationLocales(localeList)
    }
}