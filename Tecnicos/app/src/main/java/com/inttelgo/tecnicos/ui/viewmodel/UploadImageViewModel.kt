package com.inttelgo.tecnicos.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import com.inttelgo.tecnicos.logic.RetroFitServiceFactory
import com.inttelgo.tecnicos.logic.persistence.Localizacion
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
import java.util.Locale

class UploadImageViewModel : ViewModel(){

    private val locationService: Localizacion= Localizacion()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage

    @RequiresApi(Build.VERSION_CODES.P)
    fun uploadImage(
        context: Context,
        imagesUpload: MutableState<List<Uri?>>,
        observation: MutableState<String>,
        sucess: MutableState<Boolean>,
        idTicket: String,
        type: String,
        idTec: String,
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
                val regex = "[^0-9]".toRegex()
                if(result < 0 && regex.containsMatchIn(result.toString())){
                    _errorMessage.value = "Error en la consulta de la base de datos"
                }else{
                    val result2 = locationService.getUserLocation(context)

                    if(result2 != null){
                        Log.d("ubication Latitud", result2.latitude.toString())
                        Log.d("ubication Longitud", result2.longitude.toString())

                        var address = getAddressFromCoordinates(context, result2.latitude, result2.longitude)

                        if (address != null) {
                            address = trimAfterThirdComma(address)
                            Log.d("ubication Dirección", address)
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
                                        .addFormDataPart("ubication", address)
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
                                        Log.d("Upload Image 2", "$uri: $responseBody")
                                        results.add("Success for $uri: $responseBody")
                                    } else {
                                        Log.d("Upload Image 3", "$uri: ${response.code}")
                                        results.add("Error for $uri: ${response.code}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("Upload Image Error", "$uri: ${e.localizedMessage}", e)
                                    results.add("Error for $uri: ${e.localizedMessage}")
                                }
                            }
                        } else {
                            Log.d("ubication Dirección", "No se pudo obtener la dirección.")
                        }

                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uriToFile(context: Context, uri: Uri, idSupport: String, currentDate: LocalDateTime): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (originalBitmap == null) {
            throw IllegalArgumentException("No se pudo decodificar el bitmap de la URI proporcionada")
        }

        //tamaño máximo permitido
        val maxWidth = 800
        val maxHeight = 800

        // Escala proporcional
        // Condicion: Imagen acostada (Horizontal)  si no la cumple realiza el reescalado normalmenteImagen vertical o cuadrada
        val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        // la funcion pair realiza un retorno de dos valores posibles, en este caso el ancho y el alto
        val (targetWidth, targetHeight) = if (originalBitmap.width > originalBitmap.height) Pair(maxWidth, (maxWidth / aspectRatio).toInt()) else Pair((maxHeight * aspectRatio).toInt(), maxHeight)

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

        val file = File(
            context.cacheDir,
            "$idSupport ${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth} ${currentDate.hour}.${currentDate.minute}.${currentDate.second}.jpg"
        )

        val outputStream = FileOutputStream(file)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Comprime la imagen con calidad del 90%
        outputStream.close()
        return file
    }


    private fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            // Verifica si hay resultados y devuelve la dirección
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                // Combina los campos de la dirección para formar un string legible
                address.getAddressLine(0)
            } else {
                "Dirección no encontrada"
            }
        } catch (e: Exception) {
            Log.e("Geocoder Error", "Error al obtener la dirección: ${e.localizedMessage}", e)
            null
        }
    }
    private fun trimAfterThirdComma(input: String): String {
        val parts = input.split(",") // Divide el string en partes por las comas
        return if (parts.size > 3) {
            parts.take(3).joinToString(",") // Toma las primeras tres partes y las une
        } else {
            input // Si hay menos de tres comas, devuelve el string original
        }
    }
}