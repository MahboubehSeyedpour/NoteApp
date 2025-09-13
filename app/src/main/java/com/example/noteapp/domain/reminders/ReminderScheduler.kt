package com.example.noteapp.domain.reminders

interface ReminderScheduler {
    fun schedule(noteId: Long, triggerAtMillis: Long, title: String, body: String?)
    fun cancel(noteId: Long)
}
