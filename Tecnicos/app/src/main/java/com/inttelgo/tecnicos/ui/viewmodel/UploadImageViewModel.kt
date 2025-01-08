package com.inttelgo.tecnicos.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import com.inttelgo.tecnicos.logic.RetroFitServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UploadImageViewModel : ViewModel(){

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadImage(
        context: Context,
        imagesUpload: MutableState<List<Uri?>>,
        observation: MutableState<String>,
        sucess: MutableState<Boolean>,
        idTicket: String,
        type: String,
        idTec: String
    ){
        if(observation.value.isEmpty() || imagesUpload.value.isEmpty()){
            _errorMessage.value = "Todos los campos son requeridos"
        }else{
            val service = RetroFitServiceFactory.makeRetroFitService()
            val results = mutableListOf<String>()
            viewModelScope.launch(Dispatchers.IO) {
                val client = OkHttpClient() // Usa el cliente fuera del bucle
                val result = service.setObs("https://app.inttelgo.com/Tecnicos/" +
                        "?pid=${RetroFitService.encodeToBase64("pages/obs_ticket.php")}" +
                        "&obs='${observation.value}'" +
                        "&idT=$idTicket" +
                        "&date='${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}'" +
                        "&tipo=$type"+
                        "&idTec=$idTec"
                )
                for (uri in imagesUpload.value) {
                    val currentDate = LocalDateTime.now()
                    if (uri == null) continue // Ignora valores nulos en la lista
                    try {
                        // Convertir URI a archivo
                        val file = uriToFile(context, uri, idTicket, currentDate)

                        // Crear cuerpo de solicitud
                        val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "image",
                                file.name,
                                RequestBody.create("image/*".toMediaType(), file)
                            )
                            .addFormDataPart("idObs", result.toString())
                            .addFormDataPart("date",currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .addFormDataPart("tipo", type)
                            .addFormDataPart("idTec", idTec)
                            .addFormDataPart("idIn", idTicket)
                            .build()
                        // Crear solicitud HTTP
                        val request = Request.Builder()
                            .url("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/upload.php")}")
                            .post(requestBody)
                            .build()

                        // Ejecutar solicitud
                        val response = client.newCall(request).execute()
                        val responseBody = response.body?.string() ?: "No response body"

                        if (response.isSuccessful) {
                            sucess.value = true
                            Log.d("Upload Image", "$uri: $responseBody")
                            results.add("Success for $uri: $responseBody")
                        } else {
                            Log.d("Upload Image", "$uri: ${response.code}")
                            results.add("Error for $uri: ${response.code}")
                        }
                    } catch (e: Exception) {
                        Log.e("Upload Image Error", "$uri: ${e.localizedMessage}", e)
                        results.add("Error for $uri: ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uriToFile(context: Context, uri: Uri, idSupport: String, currentDate: LocalDateTime): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "$idSupport ${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth} ${currentDate.hour}.${currentDate.minute}.${currentDate.second} .jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}