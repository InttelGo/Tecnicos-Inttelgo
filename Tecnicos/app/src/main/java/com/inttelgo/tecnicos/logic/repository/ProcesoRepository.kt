package com.inttelgo.tecnicos.logic.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.inttelgo.tecnicos.logic.Model.CreateEvidenciaInstalationResponse
import com.inttelgo.tecnicos.logic.Model.FinishInstalationResponse
import com.inttelgo.tecnicos.logic.Model.Request.AddInventaryInstalacionRequest
import com.inttelgo.tecnicos.logic.Model.Response.ObservacionResponse
import com.inttelgo.tecnicos.logic.Model.updateInstallationBody
import com.inttelgo.tecnicos.logic.process.ImageOperations
import com.inttelgo.tecnicos.network.HttpRetry
import com.inttelgo.tecnicos.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ProcesoRepository {
    private val tag = "ProcesoRepository"
    private val imageOperations = ImageOperations()

    suspend fun consultById(id: String) = RetrofitClient.api.instalacionByID(id)

    suspend fun consultWithFilter(
        filters: String,
        pagination: Int,
        limit: Int,
        sorting: String
    ) = RetrofitClient.api.processWithFilter(
        filters, pagination, limit, sorting
    )

    suspend fun consultObsWithFilterAndId(
        id: String,
        filters: String,
        pagination: Int,
        limit: Int,
        sorting: String
    ) = RetrofitClient.api.consultObsInstalacionWithFilter(
        id, filters, pagination, limit, sorting
    )

    suspend fun consultByObsInstalacion(id: String, idObservacion: String) =
        RetrofitClient.api.consultByObsInstalacion(id, idObservacion)

    suspend fun update(
        id: String,
        body: updateInstallationBody,
    ) = RetrofitClient.api.update(
        id,
        body
    )

    suspend fun  getArticulosInstalacion(id: String) = RetrofitClient.api.getArticulosInstalacion(id)

    suspend fun  agregarInventarioInstalacion (addInventaryInstalacionRequest: AddInventaryInstalacionRequest) = RetrofitClient.api.agregarInventarioInstalacion(addInventaryInstalacionRequest)

    suspend fun consultEvicencias(id: String) = RetrofitClient.api.consultEvicenciasInstalation(id)

    suspend fun createEvidencia(
        id: String,
        image: Uri,
        latitud: Double,
        longitud: Double,
        context: Context
    ): Response<CreateEvidenciaInstalationResponse> {
        val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val mediaParts = mutableListOf<MultipartBody.Part>()

        try {
            val inputStream = context.contentResolver.openInputStream(image)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                val mimeType = context.contentResolver.getType(image) ?: "image/jpeg"
                val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val fileName = imageOperations.getFileName(context, image) ?: "image_1.jpg"
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

        return RetrofitClient.api.createEvidenciaInstalation(
            id,
            latitud = latitudBody,
            longitud = longitudBody,
            skipProcess = imageOperations.skipProcessRequestBody(true).takeIf { mediaParts.isNotEmpty() },
            media = mediaParts.ifEmpty { null }
        )
    }

    suspend fun createObs(
        id: String,
        observacion: String,
        images: List<Uri?>,
        latitud: Double,
        longitud: Double,
        context: Context
    ): Response<ObservacionResponse> {
        val observacionBody = observacion.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
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
                Log.e(tag, "Error al procesar imagen: ${e.message}")
            }
        }

        return RetrofitClient.api.createObsInstalacion(
            id = id,
            observacion = observacionBody,
            latitud = latitudBody,
            longitud = longitudBody,
            skipProcess = imageOperations.skipProcessRequestBody(true).takeIf { mediaParts.isNotEmpty() },
            media = mediaParts.ifEmpty { null }
        )
    }

    suspend fun deleteImage(id : String) = RetrofitClient.api.deleteImage(id)
    suspend fun finish(
        id: String,
        observacion: String,
        signatureBitmap: Bitmap?,
        articulos: String,
        esEncargado: Boolean,
        nombreEncargado: String?,
        identificacionEncargado: String?,
        latitud: Double,
        longitud: Double
    ): Response<FinishInstalationResponse>{
        val observacionBody = observacion.toRequestBody("text/plain".toMediaTypeOrNull())
        val latitudBody = latitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val longitudBody = longitud.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val esEncargadoBody = esEncargado.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val nombreEncargadoBody = nombreEncargado?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
        val identificacionEncargadoBody = identificacionEncargado?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())

        val mediaParts = mutableListOf<MultipartBody.Part>()

        try {
            signatureBitmap?.let { bitmap ->
                mediaParts.addAll(
                    imageOperations.signatureMediaParts(
                        bitmap = bitmap,
                        fileName = "comprovante_instalacion_${id}.jpg"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("ObsTicketRepository", "Error al procesar firma: ${e.message}")
        }
        return HttpRetry.run {
            RetrofitClient.api.finishTicket(
                id = id,
                latitud = latitudBody,
                longitud = longitudBody,
                observacion = observacionBody,
                media = mediaParts.ifEmpty { null },
                articulos = articulos,
                esEncargado = esEncargadoBody,
                nombreEncargado = nombreEncargadoBody,
                identificacionEncargado = identificacionEncargadoBody,
                skipProcess = imageOperations.skipProcessRequestBody(true).takeIf { mediaParts.isNotEmpty() }
            )
        }
    }
}
