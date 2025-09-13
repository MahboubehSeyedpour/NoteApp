package com.example.noteapp.di

import com.example.noteapp.data.reminder.AlarmReminderScheduler
import com.example.noteapp.domain.reminders.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class ReminderModule {
    @Binds
    abstract fun bindRepository(alarmReminderScheduler: AlarmReminderScheduler): ReminderScheduler
}