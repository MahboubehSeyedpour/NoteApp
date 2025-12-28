package com.app.noteapp.presentation.mapper

import com.app.noteapp.domain.common_model.Note
import com.app.noteapp.presentation.model.NoteUiModel

fun List<Note>.toUiList(): List<NoteUiModel> = map { it.toUi() }