package com.example.noteapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.domain.model.Note
import com.example.noteapp.domain.repository.NoteRepository
import com.example.noteapp.presentation.screens.home.model.NoteUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<HomeEvents>()
    val events = _events.asSharedFlow()

    val notes = listOf(
        NoteUi(
            id = "1",
            title = "Heli Website Design",
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
            timeBadge = "Tomorrow, 18:00",
            categoryBadge = "Work"
        ),
        NoteUi(
            id = "2",
            title = "Heli Website Design",
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
            timeBadge = null,
            categoryBadge = null
        ),
        NoteUi(
            id = "3",
            title = "Heli Website Design",
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
            timeBadge = "Tomorrow, 18:00",
            categoryBadge = null
        ),
        NoteUi(
            id = "4",
            title = "Heli Website Design",
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
            timeBadge = "Tomorrow, 18:00",
            categoryBadge = null
        ),
        NoteUi(
            id = "5",
            title = "Heli Website Design",
            body = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt",
            timeBadge = "Tomorrow, 18:00",
            categoryBadge = "work"
        ),
    )

    init {
        viewModelScope.launch {
            noteRepository.addNote(
                Note(
                    0,
                    "note title",
                    description = "here is description",
                    pinned = false,
                    createdAt = java.util.Date().time
                )
            )
        }
    }
}