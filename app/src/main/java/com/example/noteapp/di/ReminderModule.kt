package com.example.noteapp.di

import com.example.noteapp.data.reminder.AlarmReminderScheduler
import com.example.noteapp.domain.reminders.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(
        impl: AlarmReminderScheduler
    ): ReminderScheduler
}