package com.inttelgo.tecnicos.logic.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.Response.FinishObservacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObservacionResponse
import com.inttelgo.tecnicos.logic.process.ImageOperations
import com.inttelgo.tecnicos.network.RetrofitClient
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class TareaRepository {
    suspend fun consultWitFilter (
        form: String,
        area: Int
    )= RetrofitClient.api.tareasWithFilter(
        form, area
    )

    suspend fun consultById (id: String) = RetrofitClient.api.tareaByID(id)

    suspend fun consultObsWitFilterAndId (id: String, form: String) = RetrofitClient.api.consultObsTareaWitFilterAndId(id, form)

    suspend fun consultByObsTarea (id: String) = RetrofitClient.api.consultByObsTarea(id)

    suspend fun getArticulosTarea (id: String) = RetrofitClient.api.getArticulosTarea(id)

    suspend fun createObs (
        id: String,
        observacion: String,
        images: List<Uri?>,
        latitud: Double,
        longitud: Double,
        context: Context
    ): Response<ObservacionResponse> {
        // Crear RequestBody para campos de texto
        val idBody = id.toRequestBody("text/plain".toMediaTypeOrNull())
        val observacionBody = observacion.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // Crear MultipartBody.Part para las imágenes
        val mediaParts = mutableListOf<MultipartBody.Part>()

        images.filterNotNull().forEachIndexed { index, uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    // Determinar el tipo MIME de la imagen
                    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                    val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

                    // Obtener el nombre del archivo o crear uno
                    val fileName = ImageOperations().getFileName(context, uri) ?: "image_$index.jpg"

                    val part = MultipartBody.Part.createFormData(
                        "media", // Nombre del campo que espera el servidor
                        fileName,
                        requestFile
                    )
                    mediaParts.add(part)
                }
            } catch (e: Exception) {
                Log.e("ObsTicketRepository", "Error al procesar imagen: ${e.message}")
            }
        }

        return RetrofitClient.api.createObsTarea(
            id = idBody,
            observacion = observacionBody,
            latitud = latitudBody,
            longitud = longitudBody,
            media = mediaParts.ifEmpty { null }
        )
    }

    suspend fun finishObs(
        id: String,
        observacion: String,
        images: List<Uri?>,
        latitud: Double,
        longitud: Double,
        articulos: StateFlow<List<Articulo>?>,
        context: Context
    ): Response<FinishObservacionResponse> {
        // Crear RequestBody para campos de texto
        val idBody = id.toRequestBody("text/plain".toMediaTypeOrNull())
        val observacionBody = observacion.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        var articulosjson = ""
        val articulosFiltrados = articulos.value?.filter { it.cantidad > 0 } ?: emptyList()
        if (articulosFiltrados.isNotEmpty()) {
            val gson = Gson()
            articulosjson = gson.toJson(articulosFiltrados)
        }

        // Crear MultipartBody.Part para las imágenes
        val mediaParts = mutableListOf<MultipartBody.Part>()

        images.filterNotNull().forEachIndexed { index, uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    // Determinar el tipo MIME de la imagen
                    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                    val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

                    // Obtener el nombre del archivo o crear uno
                    val fileName = ImageOperations().getFileName(context, uri) ?: "image_$index.jpg"

                    val part = MultipartBody.Part.createFormData(
                        "media", // Nombre del campo que espera el servidor
                        fileName,
                        requestFile
                    )
                    mediaParts.add(part)
                }
            } catch (e: Exception) {
                Log.e("ObsTicketRepository", "Error al procesar imagen: ${e.message}")
            }
        }

        return RetrofitClient.api.finishObsTarea(
            id = idBody,
            observacion = observacionBody,
            latitud = latitudBody,
            longitud = longitudBody,
            articulos = articulosjson,
            media = mediaParts.ifEmpty { null }
        )
    }
}