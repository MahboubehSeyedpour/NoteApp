package com.app.noteapp.presentation.mapper

import androidx.compose.ui.graphics.toArgb
import com.app.noteapp.domain.model.common_model.Note
import com.app.noteapp.domain.model.common_model.NoteBlock
import com.app.noteapp.domain.model.common_model.Reminder
import com.app.noteapp.domain.model.common_model.Tag
import com.app.noteapp.presentation.model.NoteBlockUiModel
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.ReminderUiModel
import com.app.noteapp.presentation.model.TagUiModel

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

fun NoteUiModel.toDomain(): Note {
    val domainBlocks = blocks.toNoteBlocksList()
    val domainTags = tags.toDomain(userId)
    val domainReminders = reminders.toDomain()

    return Note(
        id = id,
        userId = userId,
        directoryId = directoryId,
        title = title,
        pinned = pinned,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        blocks = domainBlocks,
        tags = domainTags,
        reminders = domainReminders
    )
}

fun NoteBlockUiModel.toDomain(): NoteBlock = when (this) {
    is NoteBlockUiModel.Text  -> this.toDomain()
    is NoteBlockUiModel.Media -> this.toDomain()
}

fun NoteBlockUiModel.Text.toDomain(): NoteBlock.Text =
    NoteBlock.Text(
        id = id,
        noteId = noteId,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        text = text
    )

fun NoteBlockUiModel.Media.toDomain(): NoteBlock.Media =
    NoteBlock.Media(
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

fun List<NoteBlockUiModel>.toNoteBlocksList(): List<NoteBlock> =
    this.sortedBy { it.position }.map { it.toDomain() }

fun TagUiModel.toDomain(
    userId: Long,
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = createdAt,
    deletedAt: Long? = null
): Tag = Tag(
    id        = id,
    userId    = userId,
    name      = name,
    colorArgb = color.toArgb(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt
)

fun List<TagUiModel>.toDomain(
    userId: Long,
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = createdAt,
    deletedAt: Long? = null
): List<Tag> =
    map { it.toDomain(userId = userId, createdAt = createdAt, updatedAt = updatedAt, deletedAt = deletedAt) }


fun ReminderUiModel.toDomain(): Reminder =
    Reminder(
        id = id,
        noteId = noteId,
        triggerAt = triggerAt,
        isEnabled = isEnabled,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

fun List<ReminderUiModel>.toDomain(): List<Reminder> =
    map { it.toDomain() }
