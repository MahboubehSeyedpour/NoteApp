package com.app.noteapp.domain.reminders

interface ReminderScheduler {
    fun schedule(noteId: Long, timeMillis: Long, title: String, description: String?)
    fun cancel(noteId: Long)
}