package com.example.noteapp.domain.reminders

interface ReminderScheduler {
    fun schedule(noteId: Long, timeMillis: Long, title: String, description: String?)
    fun cancel(noteId: Long)
}