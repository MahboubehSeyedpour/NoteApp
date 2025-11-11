package com.app.noteapp.presentation.model

import androidx.annotation.DrawableRes
import com.app.noteapp.R

enum class AvatarType { FEMALE, MALE }

@DrawableRes
fun AvatarType.iconRes(): Int = when (this) {
    AvatarType.FEMALE -> R.mipmap.img_woman_foreground
    AvatarType.MALE -> R.mipmap.img_man_foreground
}