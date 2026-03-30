package com.inttelgo.tecnicos.logic.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.inttelgo.tecnicos.logic.Model.CreateEvidenciaInstalationResponse
import com.inttelgo.tecnicos.logic.Model.FinishInstalationResponse
import com.inttelgo.tecnicos.logic.Model.Request.AddInventaryInstalacionRequest
import com.inttelgo.tecnicos.logic.Model.Request.ChangeStatusProcesosRequest
import com.inttelgo.tecnicos.logic.process.ImageOperations
import com.inttelgo.tecnicos.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ProcesoRepository {
    private val tag = "ProcesoRepository"
    suspend fun consultWithFilter(
        filters: String,
        pagination: Int,
        limit: Int,
        sorting: String
    ) = RetrofitClient.api.processWithFilter(
        filters, pagination, limit, sorting
    )

    suspend fun changeStatus(
        request: ChangeStatusProcesosRequest,
    ) = RetrofitClient.api.processChangeStatus(
        request
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
                // Determinar el tipo MIME de la imagen
                val mimeType = context.contentResolver.getType(image) ?: "image/jpeg"
                val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                // Obtener el nombre del archivo o crear uno
                val fileName = ImageOperations().getFileName(context, image) ?: "image_1.jpg"
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

        return RetrofitClient.api.createEvidenciaInstalation(
            id,
            latitud = latitudBody,
            longitud = longitudBody,
            media = mediaParts.ifEmpty { null }
        )
    }

    suspend fun deleteImage(id : String) = RetrofitClient.api.deleteImage(id)
    suspend fun finish(
        id: String,
        observacion: String,
        signatureBitmap: Bitmap?,
        context: Context,
        articulos: String
    ): Response<FinishInstalationResponse>{
        val idBody = id.toRequestBody("text/plain".toMediaTypeOrNull())
        val observacionBody = observacion.toRequestBody("text/plain".toMediaTypeOrNull())

        val mediaParts = mutableListOf<MultipartBody.Part>()

        Log.d(tag, "${idBody}  ${observacionBody} ${mediaParts} }")
        try {
            // Convertir la firma (Bitmap) a bytes y agregarla como parte multipart
            val stream = ByteArrayOutputStream()
            signatureBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val signatureBytes = stream.toByteArray()

            val requestFile = signatureBytes.toRequestBody("image/png".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData(
                "media",
                "comprovante_instalacion_${id}.png",
                requestFile
            )
            mediaParts.add(part)
        } catch (e: Exception) {
            Log.e("ObsTicketRepository", "Error al procesar firma: ${e.message}")
        }
        return RetrofitClient.api.finishTicket(
            id = idBody,
            observacion = observacionBody,
            media = mediaParts.ifEmpty { null },
            articulos
        )
    }
}