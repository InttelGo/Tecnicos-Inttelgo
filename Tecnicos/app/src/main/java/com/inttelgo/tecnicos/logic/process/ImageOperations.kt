package com.inttelgo.tecnicos.logic.process

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import com.inttelgo.tecnicos.logic.persistence.showNotification
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.core.graphics.scale

open class ImageOperations {
    val tag = "MediaCompression"
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    suspend fun uriToFile(context: Context, uri: Uri, currentDate: LocalDateTime): File? {
        Log.d(tag, uri.toString())
        return withContext(Dispatchers.IO) {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            Log.d(tag, mimeType.toString())
            when {
                mimeType?.startsWith("image") == true -> compressImage(context, uri, currentDate)
                mimeType?.startsWith("video") == true -> compressVideo(context, uri, currentDate)
                else -> throw IllegalArgumentException("Formato de archivo no compatible")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compressImage(context: Context, uri: Uri, currentDate: LocalDateTime): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        Log.d(tag, inputStream.toString() )
        Log.d(tag, originalBitmap.toString() )

        if (originalBitmap == null) {
            throw IllegalArgumentException("No se pudo decodificar el bitmap de la URI proporcionada")
        }

        val maxWidth = 800
        val maxHeight = 800
        val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        val (targetWidth, targetHeight) = if (originalBitmap.width > originalBitmap.height) {
            Pair(maxWidth, (maxWidth / aspectRatio).toInt())
        } else {
            Pair((maxHeight * aspectRatio).toInt(), maxHeight)
        }

        val resizedBitmap = originalBitmap.scale(targetWidth, targetHeight)

        val file = File(
            context.cacheDir,
            "${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth} ${currentDate.hour}.${currentDate.minute}.${currentDate.second}.jpg"
        )

        val outputStream = FileOutputStream(file)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.close()
        return file
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