package com.inttelgo.tecnicos.logic

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

object ComposeFileProvider {
    fun getImageUri(context: Context): Uri {
        val imageFile = File(context.getExternalFilesDir(null), "image.jpg")
        Log.d("Photo 2", "Prueba 2")
        return FileProvider.getUriForFile( context, "${context.packageName}.fileprovider", imageFile )
    }
}