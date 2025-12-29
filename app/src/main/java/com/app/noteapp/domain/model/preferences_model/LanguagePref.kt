package com.app.noteapp.domain.model.preferences_model

enum class LanguagePref(val prefValue: String) {
    FA("fa"),
    EN("en");

    companion object {
        fun fromPref(value: String?): LanguagePref =
            when (value) {
                EN.prefValue  -> EN
                FA.prefValue  -> FA
                null          -> FA   // DEFAULT = FARSI
                else          -> FA   // fallback
            }
    }
}