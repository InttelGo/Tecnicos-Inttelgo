package com.inttelgo.tecnicos.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class UploadImageViewModel : ViewModel(){

    fun uploadImage(
        context: Context,
        imagesUpload: MutableState<List<Uri?>>,
        onResult: (String) -> Unit
    ): MutableList<String> {
        val results = mutableListOf<String>()
        viewModelScope.launch(Dispatchers.IO) {
            for(uri in imagesUpload.value){
                try {
                    // Convert URI to file
                    val file = uriToFile(context, uri!!)
                    val client = OkHttpClient()

                    // Create request body
                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", file.name, RequestBody.create("image/*".toMediaType(), file))
                        .build()

                    // Create request
                    val request = Request.Builder()
                        .url("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/upload.php")}") // Change URL to your server's
                        .post(requestBody)
                        .build()
                    Log.d("Upload Image", request.url.toString())

                    // Execute request
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Log.d("Upload Image 1", "$uri: ${response.body?.string()}")
                        results.add("Success for $uri: ${response.body?.string()}")
                    } else {
                        Log.d("Upload Image 2", "$uri: ${response.code}")
                        results.add("Error for $uri: ${response.code}")
                    }
                } catch (e: Exception) {
                    Log.e("Upload Image 3", "$uri: ${e.localizedMessage}", e)
                    results.add("Error for $uri: ${e.localizedMessage}")
                }
            }
            onResult(results.joinToString("\n"))
            }
        return results
        }


    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}