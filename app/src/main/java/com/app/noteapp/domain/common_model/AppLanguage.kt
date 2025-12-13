package com.app.noteapp.domain.common_model


enum class AppLanguage(val prefValue: String) {
    FA("fa"),
    EN("en");

    companion object {
        fun fromPref(value: String?): AppLanguage =
            when (value) {
                EN.prefValue  -> EN
                FA.prefValue  -> FA
                null          -> FA   // DEFAULT = FARSI
                else          -> FA   // Fallback also FARSI
            }
    }
}
