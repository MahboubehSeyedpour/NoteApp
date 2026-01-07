package com.app.noteapp.presentation.screens.home.contract

import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.domain.model.preferences_model.AvatarPref
import com.app.noteapp.domain.model.preferences_model.LanguagePref
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.SortOrder
import com.app.noteapp.presentation.model.TagUiModel
import com.app.noteapp.presentation.screens.home.ALL_TAG
import com.app.noteapp.presentation.screens.home.ALL_TAG_ID
import com.app.noteapp.presentation.screens.home.TimeFilter

data class HomeUiState(
    val sortOrder: SortOrder = SortOrder.DESC,
    val layoutMode: LayoutMode = LayoutMode.LIST,
    val searchQuery: String = "",
    val selectedTagId: Long = ALL_TAG_ID,
    val onlyReminder: Boolean = false,
    val timeFilter: TimeFilter = TimeFilter.ALL,
    val rangeStart: Long? = null,
    val rangeEnd: Long? = null,
    val selectedIds: Set<Long> = emptySet(),
    val language: LanguagePref = LanguagePref.FA,
    val avatar: AvatarPref = AvatarPref.MALE,
    val tags: List<TagUiModel> = listOf(ALL_TAG),
    val notes: List<NoteUiModel> = listOf()
) {
    val isFilterActive: Boolean
        get() = selectedTagId != ALL_TAG_ID ||
                onlyReminder ||
                timeFilter != TimeFilter.ALL ||
                rangeStart != null ||
                rangeEnd != null
}