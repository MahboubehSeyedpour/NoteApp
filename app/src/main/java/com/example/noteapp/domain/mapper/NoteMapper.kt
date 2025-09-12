package com.example.noteapp.domain.mapper

import com.example.noteapp.domain.model.Note
import com.example.noteapp.presentation.screens.home.model.NoteUi

fun Note.toUi(): NoteUi = NoteUi(
    id = id.toString(),
    title = title,
    body = description.orEmpty(),
    timeBadge = null,
    categoryBadge = null
)
