package com.app.noteapp.presentation.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class BottomSheetRowModel(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val value: @Composable RowScope.() -> Unit,
    val onRowClicked: () -> Unit
)