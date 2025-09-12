package com.example.noteapp.domain.mapper

import com.example.noteapp.domain.model.Note
import com.example.noteapp.presentation.model.NoteUI

fun Note.toUI(): NoteUI = NoteUI(
    id = id,
    title = title,
    description = description,
    categoryBadge = category,
    timeBadge = time,
    createdAt = createdAt
)

fun NoteUI.toDomain(): Note = Note(
    id = id,
    title = title,
    description = description,
    pinned = false,             // set from UI if you support it
    createdAt = createdAt,
    category = categoryBadge,
    time = timeBadge,
)
