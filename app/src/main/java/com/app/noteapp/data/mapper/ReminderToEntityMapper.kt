package com.app.noteapp.data.mapper

import com.app.noteapp.data.local.entity.ReminderEntity
import com.app.noteapp.data.utils.normalizeTs
import com.app.noteapp.domain.common_model.Reminder

fun Reminder.toEntity(
    serverId: String? = null,
    version: Long = 0,
    dirty: Boolean = true,
    now: Long = System.currentTimeMillis()
): ReminderEntity {
    require(triggerAt > 0L) { "Reminder.triggerAt must be > 0" }

    return ReminderEntity(
        id = id,
        noteId = noteId,
        triggerAt = triggerAt,
        isEnabled = isEnabled,
        createdAt = normalizeTs(createdAt, now),
        updatedAt = normalizeTs(updatedAt, now),
        deletedAt = deletedAt,
        serverId = serverId,
        version = version,
        dirty = dirty
    )
}