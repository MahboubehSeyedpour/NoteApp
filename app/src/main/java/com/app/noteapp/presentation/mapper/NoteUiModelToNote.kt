package com.app.noteapp.presentation.mapper

import com.app.noteapp.domain.model.common_model.Note
import com.app.noteapp.domain.model.common_model.NoteBlock
import com.app.noteapp.presentation.model.NoteBlockUiModel
import com.app.noteapp.presentation.model.NoteUiModel

fun Note.toUi(): NoteUiModel {
    val uiBlocks = blocks.toUiList()
    val uiTags = tags.toUiList()
    val uiReminders = reminders.toUiList()

    val previewText = uiBlocks
        .filterIsInstance<NoteBlockUiModel.Text>()
        .firstOrNull()
        ?.text
        ?.take(200)

    return NoteUiModel(
        id = id,
        userId = userId,
        directoryId = directoryId,
        title = title,
        pinned = pinned,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        blocks = uiBlocks,
        tags = uiTags,
        reminders = uiReminders
    )
}

fun NoteBlock.toUi(): NoteBlockUiModel = when (this) {
    is NoteBlock.Text -> this.toUi()
    is NoteBlock.Media -> this.toUi()
}

fun NoteBlock.Text.toUi(): NoteBlockUiModel.Text =
    NoteBlockUiModel.Text(
        id = id,
        noteId = noteId,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        text = text
    )

fun NoteBlock.Media.toUi(): NoteBlockUiModel.Media =
    NoteBlockUiModel.Media(
        id = id,
        noteId = noteId,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        kind = kind,
        localUri = localUri,
        mimeType = mimeType,
        widthPx = widthPx,
        heightPx = heightPx,
        durationMs = durationMs,
        sizeBytes = sizeBytes
    )

fun List<NoteBlock>.toUiList(): List<NoteBlockUiModel> =
    this.sortedBy { it.position }.map { it.toUi() }