package com.app.noteapp.presentation.screens.home

import com.app.noteapp.core.enums.LayoutMode
import com.app.noteapp.domain.common_model.AppLanguage
import com.app.noteapp.domain.common_model.AvatarType
import com.app.noteapp.presentation.model.NoteUiModel
import com.app.noteapp.presentation.model.SortOrder
import com.app.noteapp.presentation.model.TagUiModel

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
    val language: AppLanguage = AppLanguage.FA,
    val avatar: AvatarType = AvatarType.MALE,
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