package com.app.noteapp.presentation.screens.add_note

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import dagger.hilt.android.qualifiers.ApplicationContext

@Composable
fun PickImageLauncher(@ApplicationContext context: Context, onImagePicked: (Uri?) -> Unit) {
    rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data
        if (uri != null) {
            val contentResolver = context.contentResolver
            val flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flags)
            onImagePicked(uri)
        }
    }
}