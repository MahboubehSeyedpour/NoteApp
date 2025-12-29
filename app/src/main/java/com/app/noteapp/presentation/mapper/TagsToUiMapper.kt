package com.app.noteapp.presentation.mapper

import com.app.noteapp.domain.model.common_model.Tag
import com.app.noteapp.presentation.model.TagUiModel

fun List<Tag>.toUiList(): List<TagUiModel> = map { it.toUi() }