package com.inttelgo.tecnicos.logic.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.Response.FinishObservacionResponse
import com.inttelgo.tecnicos.logic.Model.Response.ObservacionResponse
import com.inttelgo.tecnicos.logic.process.ImageOperations
import com.inttelgo.tecnicos.network.HttpRetry
import com.inttelgo.tecnicos.network.RetrofitClient
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream

class TareaRepository {
    private val imageOperations = ImageOperations()

    suspend fun consultWitFilter (
        filters: String,
        pagination: Int,
        limit: Int,
        sorting: String
    )= RetrofitClient.api.tareasWithFilter(
        filters, pagination, limit, sorting
    )

    suspend fun consultById (id: String) = RetrofitClient.api.tareaByID(id)

    suspend fun consultObsWitFilterAndId (
        id: String,
        filters: String,
        pagination: Int,
        limit: Int,
        sorting: String
    ) = RetrofitClient.api.consultObsTareaWitFilterAndId(
        id,
        filters,
        pagination,
        limit,
        sorting
    )

    suspend fun consultByObsTarea(idTarea: String, idObservacion: String) =
        RetrofitClient.api.consultByObsTarea(idTarea, idObservacion)

    suspend fun getArticulosTarea (id: String) = RetrofitClient.api.getArticulosTarea(id)

    suspend fun createObs (
        id: String,
        observacion: String,
        images: List<Uri?>,
        latitud: Double,
        longitud: Double,
        context: Context,
        signatureBitmap: Bitmap?,
        esEncargado: Boolean,
        nombreEncargado: String?,
        identificacionEncargado: String?
    ): Response<ObservacionResponse> {
        val observacionBody = observacion.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val esEncargadoBody = esEncargado.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val nombreEncargadoBody = nombreEncargado?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
        val identificacionEncargadoBody = identificacionEncargado?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())

        val mediaParts = mutableListOf<MultipartBody.Part>()

        images.filterNotNull().forEachIndexed { index, uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                    val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val fileName = imageOperations.getFileName(context, uri) ?: "image_$index.jpg"
                    mediaParts.addAll(
                        imageOperations.createMediaPart(
                            fileName = fileName,
                            requestBody = requestFile,
                            skipProcess = true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("ObsTicketRepository", "Error al procesar imagen: ${e.message}")
            }
        }

        signatureBitmap?.let { bitmap ->
            try {
                mediaParts.addAll(
                    imageOperations.signatureMediaParts(
                        bitmap = bitmap,
                        fileName = "firma_tarea_${id}.jpg"
                    )
                )
            } catch (e: Exception) {
                Log.e("TareaRepository", "Error al procesar firma: ${e.message}")
            }
        }

        return RetrofitClient.api.createObsTarea(
            id = id,
            observacion = observacionBody,
            latitud = latitudBody,
            longitud = longitudBody,
            esEncargado = esEncargadoBody,
            nombreEncargado = nombreEncargadoBody,
            identificacionEncargado = identificacionEncargadoBody,
            skipProcess = imageOperations.skipProcessRequestBody(true).takeIf { mediaParts.isNotEmpty() },
            media = mediaParts.ifEmpty { null }
        )
    }

    suspend fun finishObs(
        id: String,
        observacion: String,
        images: List<Uri?>,
        latitud: Double,
        longitud: Double,
        signatureBitmap: Bitmap?,
        articulos: StateFlow<List<Articulo>?>,
        context: Context,
        esEncargado: Boolean,
        nombreEncargado: String?,
        identificacionEncargado: String?
    ): Response<FinishObservacionResponse> {
        val observacionBody = observacion.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val esEncargadoBody = esEncargado.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val nombreEncargadoBody = nombreEncargado?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
        val identificacionEncargadoBody = identificacionEncargado?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
        var articulosjson = ""
        val articulosFiltrados = articulos.value?.filter { it.cantidad > 0 } ?: emptyList()
        if (articulosFiltrados.isNotEmpty()) {
            val gson = Gson()
            articulosjson = gson.toJson(articulosFiltrados)
        }

        val mediaParts = mutableListOf<MultipartBody.Part>()

        images.filterNotNull().forEachIndexed { index, uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                    val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                    val fileName = imageOperations.getFileName(context, uri) ?: "image_$index.jpg"
                    mediaParts.addAll(
                        imageOperations.createMediaPart(
                            fileName = fileName,
                            requestBody = requestFile,
                            skipProcess = true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("ObsTicketRepository", "Error al procesar imagen: ${e.message}")
            }
        }

        signatureBitmap?.let { bitmap ->
            try {
                mediaParts.addAll(
                    imageOperations.signatureMediaParts(
                        bitmap = bitmap,
                        fileName = "firma_tarea_${id}.jpg"
                    )
                )
            } catch (e: Exception) {
                Log.e("TareaRepository", "Error al procesar firma: ${e.message}")
            }
        }

        return HttpRetry.run {
            RetrofitClient.api.finishObsTarea(
                id = id,
                observacion = observacionBody,
                latitud = latitudBody,
                longitud = longitudBody,
                articulos = articulosjson,
                esEncargado = esEncargadoBody,
                nombreEncargado = nombreEncargadoBody,
                identificacionEncargado = identificacionEncargadoBody,
                skipProcess = imageOperations.skipProcessRequestBody(true).takeIf { mediaParts.isNotEmpty() },
                media = mediaParts.ifEmpty { null }
            )
        }
    }
}
