package com.app.noteapp.presentation.mapper

import com.app.noteapp.domain.model.common_model.Reminder
import com.app.noteapp.presentation.model.ReminderUiModel

fun List<Reminder>.toUiList(): List<ReminderUiModel> = map { it.toUi() }