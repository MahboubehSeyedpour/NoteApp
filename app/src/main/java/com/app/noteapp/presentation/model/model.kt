package com.app.noteapp.presentation.model

import androidx.annotation.DrawableRes
import com.app.noteapp.R
import com.app.noteapp.domain.model.preferences_model.AvatarPref

@DrawableRes
fun AvatarPref.iconRes(): Int = when (this) {
    AvatarPref.FEMALE -> R.mipmap.img_woman_foreground
    AvatarPref.MALE -> R.mipmap.img_man_foreground
}