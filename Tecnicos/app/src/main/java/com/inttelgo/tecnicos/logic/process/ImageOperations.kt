package com.inttelgo.tecnicos.logic.process

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import com.inttelgo.tecnicos.logic.persistence.showNotification
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class ImageOperations {
    val tag = "MediaCompression"

    companion object {
        const val MAX_IMAGE_EDGE = 1024
        const val MAX_IMAGE_BYTES = 350L * 1024L
        const val JPEG_QUALITY = 65

        fun newCaptureImageUri(context: Context): Uri {
            val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }
    }

    /**
     * Crea la parte multipart de un archivo multimedia.
     * Usar junto con [skipProcessRequestBody] para indicar al servidor
     * que omita el reprocesamiento (la app ya comprimió el archivo).
     */
    fun createMediaPart(
        fileName: String,
        requestBody: RequestBody,
        skipProcess: Boolean = true,
        mediaFieldName: String = "media"
    ): List<MultipartBody.Part> {
        return listOf(
            MultipartBody.Part.createFormData(mediaFieldName, fileName, requestBody),
            // Propiedad asociada a esta foto para que el servidor omita el doble procesamiento
            MultipartBody.Part.createFormData("skipProcess", skipProcess.toString())
        )
    }

    fun skipProcessRequestBody(skipProcess: Boolean = true): RequestBody =
        skipProcess.toString().toRequestBody("text/plain".toMediaTypeOrNull())

    /** Firma más liviana para redes 4G/hotspot (PNG sin comprimir suele pesar 1–4 MB). */
    fun signatureMediaParts(
        bitmap: Bitmap,
        fileName: String,
        skipProcess: Boolean = true
    ): List<MultipartBody.Part> {
        val maxWidth = 1280
        val scaled = if (bitmap.width > maxWidth) {
            val ratio = maxWidth.toFloat() / bitmap.width
            bitmap.scale((bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt())
        } else {
            bitmap
        }
        val stream = java.io.ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 75, stream)
        val requestFile = stream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val jpegName = if (fileName.endsWith(".png", ignoreCase = true)) {
            fileName.replace(".png", ".jpg", ignoreCase = true)
        } else {
            fileName
        }
        return createMediaPart(jpegName, requestFile, skipProcess)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    suspend fun uriToFile(context: Context, uri: Uri, currentDate: LocalDateTime): File? {
        Log.d(tag, uri.toString())
        return withContext(Dispatchers.IO) {
            waitUntilFileHasContent(context, uri)
            val mimeType = context.contentResolver.getType(uri)
            val fileName = getFileName(context, uri)
            Log.d(tag, "mime=$mimeType name=$fileName")
            when {
                isVideo(mimeType, fileName) -> compressVideo(context, uri, currentDate)
                else -> compressImage(context, uri, currentDate)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compressImage(context: Context, uri: Uri, currentDate: LocalDateTime): File {
        val source = copyUriToCache(context, uri, ".jpg")
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(source.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw IllegalArgumentException("No se pudo leer la imagen")
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, MAX_IMAGE_EDGE)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decoded = BitmapFactory.decodeFile(source.absolutePath, decodeOptions)
            ?: throw IllegalArgumentException("No se pudo decodificar la imagen")
        val oriented = applyExifRotation(source, decoded)

        val (targetWidth, targetHeight) = targetSize(oriented.width, oriented.height, MAX_IMAGE_EDGE)
        val resized = if (oriented.width == targetWidth && oriented.height == targetHeight) {
            oriented
        } else {
            oriented.scale(targetWidth.coerceAtLeast(1), targetHeight.coerceAtLeast(1))
        }

        val file = File(
            context.cacheDir,
            "${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth}_" +
                "${currentDate.hour}.${currentDate.minute}.${currentDate.second}.jpg"
        )

        var quality = JPEG_QUALITY
        do {
            FileOutputStream(file).use { outputStream ->
                resized.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            Log.d(tag, "Foto comprimida: ${file.length() / 1024}KB q=$quality ${resized.width}x${resized.height}")
            quality -= 15
        } while (file.length() > MAX_IMAGE_BYTES && quality >= 40)

        if (file.length() <= 0L) {
            throw IllegalArgumentException("La compresión de la imagen quedó vacía")
        }

        source.delete()
        return file
    }

    private suspend fun waitUntilFileHasContent(context: Context, uri: Uri) {
        repeat(8) { attempt ->
            val size = try {
                context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0L
            } catch (_: Exception) {
                0L
            }
            // -1 = tamaño desconocido (válido); 0 = archivo aún vacío
            if (size != 0L) return
            delay(150L * (attempt + 1))
        }
    }

    private fun copyUriToCache(context: Context, uri: Uri, suffix: String): File {
        val target = File(context.cacheDir, "src_${System.currentTimeMillis()}$suffix")
        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("No se pudo abrir el archivo capturado")
        input.use { incoming ->
            FileOutputStream(target).use { outgoing -> incoming.copyTo(outgoing) }
        }
        if (target.length() <= 0L) {
            throw IllegalArgumentException("El archivo capturado está vacío")
        }
        return target
    }

    private fun isVideo(mimeType: String?, fileName: String?): Boolean {
        if (mimeType?.startsWith("video") == true) return true
        val name = fileName?.lowercase() ?: return false
        return name.endsWith(".mp4") || name.endsWith(".3gp") || name.endsWith(".mkv") ||
            name.endsWith(".mov") || name.endsWith(".webm")
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxEdge: Int): Int {
        var inSampleSize = 1
        var halfWidth = width / 2
        var halfHeight = height / 2
        while (halfWidth / inSampleSize >= maxEdge && halfHeight / inSampleSize >= maxEdge) {
            inSampleSize *= 2
        }
        return inSampleSize.coerceAtLeast(1)
    }

    private fun targetSize(width: Int, height: Int, maxEdge: Int): Pair<Int, Int> {
        if (width <= maxEdge && height <= maxEdge) return width to height
        val ratio = width.toFloat() / height.toFloat()
        return if (width >= height) {
            maxEdge to (maxEdge / ratio).toInt().coerceAtLeast(1)
        } else {
            (maxEdge * ratio).toInt().coerceAtLeast(1) to maxEdge
        }
    }

    private fun applyExifRotation(source: File, bitmap: Bitmap): Bitmap {
        val rotation = try {
            val exif = ExifInterface(source.absolutePath)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (_: Exception) {
            0f
        }
        if (rotation == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(rotation) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    @SuppressLint("NewApi")
    private suspend fun compressVideo(
        context: Context,
        inputUri: Uri,
        currentDate: LocalDateTime,
    ): File? {
        return suspendCoroutine { continuation ->
            try {
                val outputFile = File(
                    context.cacheDir,
                    "${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth}-" +
                            "${currentDate.hour}.${currentDate.minute}.${currentDate.second}.mp4"
                )


                val videoStrategy = DefaultVideoStrategy.Builder()
                    .keyFrameInterval(3f) // Keyframe cada 3 segundos
                    .bitRate(1_500_000L) // 1.5 Mbps
                    .frameRate(30) // 30 FPS
                    .build()


                Transcoder.into(outputFile.absolutePath)
                    .addDataSource(context, inputUri)
                    .setVideoTrackStrategy(videoStrategy)
                    .setAudioTrackStrategy(null) // Mantener audio original
                    .setListener(object : TranscoderListener {
                        override fun onTranscodeProgress(progress: Double) {
                            Log.d(tag, "Progreso: ${(progress * 100).toInt()}%")
                        }

                        override fun onTranscodeCompleted(successCode: Int) {
                            if (successCode == Transcoder.SUCCESS_TRANSCODED) {
                                if (outputFile.exists() && outputFile.length() > 0) {
                                    val outputSize = outputFile.length() / 1024
                                    Log.d(tag, "Compresión exitosa. Tamaño final: ${outputSize}KB")

                                    showNotification(
                                        context,
                                        "Video Enviado",
                                        "El video se ha comprimido y enviado correctamente."
                                    )
                                    continuation.resume(outputFile)
                                } else {
                                    Log.e(tag, "El archivo de salida no existe o está vacío")
                                    continuation.resume(null)
                                }
                            } else {
                                Log.e(tag, "Error en transcode. Código: $successCode")
                                continuation.resume(null)
                            }
                        }

                        override fun onTranscodeCanceled() {
                            Log.e(tag, "Compresión cancelada")
                            continuation.resume(null)
                        }

                        override fun onTranscodeFailed(exception: Throwable) {
                            Log.e(tag, "Error en compresión: ${exception.message}", exception)
                            continuation.resume(null)
                        }
                    })
                    .transcode()

            } catch (e: Exception) {
                Log.e(tag, "Error general en compressVideo: ${e.message}", e)
                continuation.resume(null)
            }
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex >= 0) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.path
            val cut = fileName?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                fileName = fileName.substring(cut + 1)
            }
        }
        return fileName
    }

}