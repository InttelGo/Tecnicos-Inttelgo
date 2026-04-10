package com.inttelgo.tecnicos.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.Picture
import com.inttelgo.tecnicos.logic.Model.Plan
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import com.inttelgo.tecnicos.logic.RetroFitServiceFactory
import com.inttelgo.tecnicos.logic.persistence.Localizacion
import com.inttelgo.tecnicos.logic.persistence.showNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UploadImageViewModel : ViewModel(){

    private val locationService: Localizacion= Localizacion()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _checkTipoI = MutableStateFlow<Boolean>(false)
    val checkTipoI: StateFlow<Boolean> = _checkTipoI

    private val _isUploadingFile = MutableStateFlow(false)
    val isUploadingFile: StateFlow<Boolean> = _isUploadingFile

    private val _checkArticles = MutableStateFlow<Boolean>(false)
    val checkArticles: StateFlow<Boolean> = _checkArticles

    private val _uploadedImages = MutableStateFlow<List<Picture?>>(emptyList())
    val uploadedImagesList: StateFlow<List<Picture?>?> = _uploadedImages

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage

    private val _articles = MutableStateFlow<List<Articulo>?>(null)
    val articles: StateFlow<List<Articulo>?> = _articles

    private val _tipoI = MutableStateFlow<Plan?>(null)
    val tipoI: StateFlow<Plan?> = _tipoI

    private val _uploadImageState = MutableStateFlow(false)
    val uploadImageState: StateFlow<Boolean> = _uploadImageState


    private val TAG ="UploadImageViewModel"


    @RequiresApi(Build.VERSION_CODES.P)
    fun uploadImages(
        context: Context,
        imagesUpload: MutableState<List<Uri?>>,
        observation: MutableState<String>,
        idTicket: String,
        type: String,
        idTec: String,
        articlesState: SnapshotStateList<Articulo>,
        onUploadComplete: () -> Unit
    ) {
        var flag = false
        if (type == "Proceso") {
            for (i in articlesState.indices) {
                Log.d(
                    "Inventario uploadImage",
                    "Articulo: ${articlesState[i].descripcion} Cantidad: ${articlesState[i].cantidad}"
                )
                flag = !(articlesState[i].cantidad == 0 && i < articlesState.size - 4)
            }
        } else {
            flag = true
        }
        if (observation.value.isEmpty() || (imagesUpload.value.isEmpty() && type != "Proceso")) {
            _errorMessage.value = "Todos los campos son requeridos"
            _warningMessage.value = null
        } else {
            if (flag) {
                val service = RetroFitServiceFactory.makeRetroFitService()
                viewModelScope.launch(Dispatchers.IO) {
                    val result = modifyInstalacion(service, observation, idTicket, type, idTec)
                    Log.d(TAG, result.toString())
                    val regex = "[^0-9]".toRegex()
                    if (result < 0 && regex.containsMatchIn(result.toString())) {
                        _errorMessage.value = "Error en la consulta de la base de datos"
                    } else {
                        if (type == "Proceso") {
                            addInventary(idTicket, articlesState, service)
                            Log.d(TAG, "prueba de proceso")
                        }
                        if (type != "Proceso") {
                            Log.d(TAG, "prueba de carga")
                            generateImage(
                                context,
                                imagesUpload,
                                idTicket,
                                result,
                                type,
                                idTec,
                            ){}
                        }
                    }
                }
                if(type == "Proceso"){
                    _uploadImageState.value = true
                }
            } else {
                if (articlesState.size > 5) {
                    _warningMessage.value = "Los servicios que incluyen telefonía deben incluir al menos un splitter"
                    _errorMessage.value = null
                } else {
                    _warningMessage.value = "Todos los artículos deben estar incluidos"
                    _errorMessage.value = null
                }
            }
        }
    }

    @SuppressLint("NewApi")
    suspend fun generateImage(
        context: Context,
        imagesUpload: MutableState<List<Uri?>>,
        idTicket: String,
        idObs: Int,
        type: String,
        idTec: String,
        onUploadComplete: () -> Unit // Para redireccionar si es necesario
    ) {
        _isUploadingFile.value = true
        val client = OkHttpClient()
        val result2 = locationService.getUserLocation(context)

        if (result2 != null) {
            val totalFiles = imagesUpload.value.count { it != null }
            var uploadedFiles = 0
            Log.d(TAG, "Total de archivos a subir: $totalFiles")

            var address = getAddressFromCoordinates(context, result2.latitude, result2.longitude)
            address = address?.let { trimAfterThirdComma(it) } ?: "Ubicación desconocida"

            for (uri in imagesUpload.value) {
                val currentDate = LocalDateTime.now()
                if (uri == null) continue

                try {
                    val file = uriToFile(context, uri, idTicket, currentDate, address)
                    if (file == null) {
                        Log.e(TAG, "No se pudo preparar el archivo para subir: $uri")
                        _errorMessage.value = "No se pudo procesar el video. Intenta de nuevo o elige uno de la galería."
                        continue
                    }
                    val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", file.name, file.asRequestBody("image/*".toMediaType()))
                            .addFormDataPart("idObs", idObs.toString())
                            .addFormDataPart("date", currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .addFormDataPart("tipo", type)
                            .addFormDataPart("idTec", idTec)
                            .addFormDataPart("idIn", idTicket)
                            .addFormDataPart("ubication", address)
                            .build()


                    val request = Request.Builder()
                        .url("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/upload.php")}")
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        uploadedFiles++
                        Log.d(TAG, "Archivo subido: $uploadedFiles/$totalFiles")

                        // Si se subieron todos los archivos y NO es un soporte, salir de la vista
                        if (uploadedFiles == totalFiles && type != "Proceso") {
                            _isUploadingFile.value = false
                            _uploadImageState.value = true
                            Log.d(TAG, "🚀 Todas las imágenes fueron subidas, navegando fuera de la vista.")
                        }

                        delay(1000) // Pequeño retraso entre cada subida para estabilidad
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Error subiendo archivo: ${e.localizedMessage}", e)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private suspend fun compressVideo(
        context: Context,
        inputUri: Uri,
        idSupport: String,
        currentDate: LocalDateTime,
        address: String
    ): File? {
        _isUploadingFile.value = true
        return suspendCoroutine { continuation ->
            val inputPath = getPathFromUri(context, inputUri)
            if (inputPath == null) {
                Log.e("VideoCompression", "getPathFromUri devolvió null para $inputUri")
                _isUploadingFile.value = false
                continuation.resume(null)
                return@suspendCoroutine
            }
            val inputFile = File(inputPath)
            val inputSizeKb = inputFile.length() / 1024
            Log.d("VideoCompression", "Tamaño inicial: ${inputSizeKb}KB path=$inputPath")
            if (inputFile.length() <= 0L) {
                Log.e("VideoCompression", "Archivo de entrada vacío")
                _isUploadingFile.value = false
                continuation.resume(null)
                return@suspendCoroutine
            }
            val h264File = File(context.cacheDir, "encoded_h264_${System.currentTimeMillis()}.mp4")
            val h264Path = h264File.absolutePath
            val inPathEscaped = escapeForFfmpegPath(inputPath)
            val h264PathEscaped = escapeForFfmpegPath(h264Path)
            val encodeCommand =
                "-y -i $inPathEscaped -c:v libx264 -preset ultrafast -crf 23 -pix_fmt yuv420p -c:a aac -b:a 100k $h264PathEscaped"
            FFmpegKit.executeAsync(encodeCommand) { encodeSession ->
                if (!ReturnCode.isSuccess(encodeSession.returnCode)) {
                    Log.e(
                        "VideoCompression",
                        "Error codificación H.264: ${encodeSession.allLogsAsString}"
                    )
                    _isUploadingFile.value = false
                    continuation.resume(null)
                    return@executeAsync
                }
                Log.d("VideoCompression", "Codificación a H.264 completada")
                val safeName =
                    "$idSupport-${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth}-${currentDate.hour}.${currentDate.minute}.${currentDate.second}.mp4"
                val outputFile = File(context.cacheDir, safeName)
                val outputPath = outputFile.absolutePath
                val outputPathEscaped = escapeForFfmpegPath(outputPath)
                val textForDraw = escapeForDrawtext(address)
                val fontEscaped = escapeForFfmpegPath("/system/fonts/Roboto-Regular.ttf")
                val compressCommand =
                    "-y -i $h264PathEscaped -vf \"scale=480:854,drawtext=fontfile=$fontEscaped:text='$textForDraw':x=10:y=30:fontsize=24:fontcolor=white:box=1:boxcolor=black@0.5\" " +
                            "-vcodec libx264 -crf 28 -preset fast -b:v 800k -c:a aac -b:a 128k $outputPathEscaped"
                Log.d("VideoCompression", "compressCommand=$compressCommand")
                FFmpegKit.executeAsync(compressCommand) { compressSession ->
                    if (ReturnCode.isSuccess(compressSession.returnCode)) {
                        Log.d(
                            "VideoCompression",
                            "Tamaño final: ${outputFile.length() / 1024}KB"
                        )
                        showNotification(
                            context,
                            "Video Enviado",
                            "El video se ha comprimido correctamente."
                        )
                        _isUploadingFile.value = false
                        continuation.resume(outputFile)
                    } else {
                        Log.e(
                            "VideoCompression",
                            "Error compresión: ${compressSession.allLogsAsString}"
                        )
                        _isUploadingFile.value = false
                        continuation.resume(null)
                    }
                }
            }
        }
    }

    /** Comillas simples en FFmpeg filtergraph rompen drawtext; escapar para text='...' */
    private fun escapeForDrawtext(address: String): String {
        return address
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace(":", "\\:")
            .replace("%", "\\%")
    }
    /** Espacios y caracteres especiales en rutas de entrada/salida */
    private fun escapeForFfmpegPath(path: String): String {
        val p = path.replace("'", "'\\''")
        return "'$p'"
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val baseName = getFileName(context, uri).ifBlank {
            "video_${System.currentTimeMillis()}.mp4"
        }
        val out = File(context.cacheDir, "upload_${System.currentTimeMillis()}_$baseName")
        fun tryCopy(): Boolean {
            return try {
                val copied = context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                    FileInputStream(pfd.fileDescriptor).use { input ->
                        FileOutputStream(out).use { output -> input.copyTo(output) }
                    }
                } ?: context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(out).use { output -> input.copyTo(output) }
                }
                copied != null && out.exists() && out.length() > 8 * 1024L
            } catch (e: Exception) {
                Log.e("VideoCompression", "copy failed uri=$uri", e)
                false
            }
        }
        repeat(10) {
            if (tryCopy()) return out.absolutePath
            Thread.sleep(200L)
        }
        out.delete()
        return null
    }
    private fun getFileName(context: Context, uri: Uri): String {
        var name = "temp_file.mp4"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }

    private suspend fun addInventary(
        idTicket: String,
        articlesState: SnapshotStateList<Articulo>,
        service: RetroFitService
    ){
        articlesState.forEach { article ->
            service.setInventary("https://app.inttelgo.com/Tecnicos/" +
                    "?pid=${RetroFitService.encodeToBase64("pages/ticket.php")}" +
                    "&idArticle=${article.id_articulo}" +
                    "&cantidad=${article.cantidad}" +
                    "&idIn=$idTicket")
        }
    }
    @SuppressLint("NewApi", "DefaultLocale")
    suspend fun modifyInstalacion(
        service: RetroFitService,
        observation: MutableState<String>,
        idTicket: String,
        type: String,
        idTec: String
    ): Int {
        if(type=="Proceso"){
            return service.setObs("https://app.inttelgo.com/Tecnicos/" +
                    "?pid=${RetroFitService.encodeToBase64("pages/process.php")}" +
                    "&obs='${observation.value}'" +
                    "&idT=$idTicket" +
                    "&idTec=$idTec"+
                    "&date='${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}'"
            )
        }else{
            Log.d(TAG, "https://app.inttelgo.com/Tecnicos/" +
                    "?pid=${RetroFitService.encodeToBase64("pages/obs_ticket.php")}" +
                    "&obs='${observation.value}'" +
                    "&idT=$idTicket" +
                    "&date='${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}'" +
                    "&tipo=$type"+
                    "&idTec=$idTec")
            return service.setObs("https://app.inttelgo.com/Tecnicos/" +
                    "?pid=${RetroFitService.encodeToBase64("pages/obs_ticket.php")}" +
                    "&obs='${observation.value}'" +
                    "&idT=$idTicket" +
                    "&date='${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}'" +
                    "&tipo=$type"+
                    "&idTec=$idTec"
            )
        }
    }

    fun getArticles(type: String){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch(Dispatchers.IO) {
            val result = service.getArticles("https://app.inttelgo.com/Tecnicos/" +
                    "?pid=${RetroFitService.encodeToBase64("pages/articulo.php")}&type=$type")
            if(result.success){
                _checkArticles.value = result.success
                _articles.value = result.articulos
                Log.d(TAG, result.success.toString())
                Log.d(TAG, result.articulos.toString())
            }else{
                _errorMessage.value = "Error en la consulta de la base de datos"
            }
        }
    }

    fun getImages(id: String){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                Log.d(TAG, "https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/image.php")}&id=$id")
                val result = service.getPictures("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/image.php")}&id=$id")
                _uploadedImages.value = result.pictures
                Log.d(TAG, "Ticket: ${result.pictures}")
            }catch ( e: Exception ){
                _errorMessage.value = e.message
            }
        }
    }

    fun getTypeI(id: String){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "https://app.inttelgo.com/Tecnicos/"+"?pid=${RetroFitService.encodeToBase64("pages/process.php")}&id=$id")
            Log.d("getArticles", "https://app.inttelgo.com/Tecnicos/" +
                    "?pid=${RetroFitService.encodeToBase64("pages/image.php")}&id=$id")
            val result = service.getTypeI("https://app.inttelgo.com/Tecnicos/" +
                    "?pid=${RetroFitService.encodeToBase64("pages/process.php")}&id=$id")
            if(result.success){
                _checkTipoI.value = result.success
                _tipoI.value = result.plan
                Log.d(TAG, result.plan.toString())
                Log.d(TAG, result.success.toString())
            }else{
                _errorMessage.value = "Error en la consulta de la base de datos"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun uriToFile(
        context: Context,
        uri: Uri,
        idSupport: String,
        currentDate: LocalDateTime,
        adress: String
    ): File? {
        val contentResolver = context.contentResolver
        val name = getFileName(context, uri)
        val mimeType = contentResolver.getType(uri) ?: guessMimeFromName(name)

        return when {
            mimeType?.startsWith("image") == true ->
                compressImage(context, uri, idSupport, currentDate)
            mimeType?.startsWith("video") == true ->
                compressVideo(context, uri, idSupport, currentDate, adress)
            else -> {
                // último recurso: por extensión, la cámara a veces no informa tipo
                when {
                    name.lowercase(Locale.getDefault()).let { n ->
                        n.endsWith(".mp4") || n.endsWith(".m4v") || n.endsWith(".3gp")
                    } -> compressVideo(context, uri, idSupport, currentDate, adress)
                    name.lowercase(Locale.getDefault()).let { n ->
                        n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png")
                    } -> compressImage(context, uri, idSupport, currentDate)
                    else -> throw IllegalArgumentException("Formato de archivo no compatible (tipo=$mimeType, nombre=$name)")
                }
            }
        }
    }

    private fun guessMimeFromName(name: String): String? {
        val lower = name.lowercase(Locale.getDefault())
        return when {
            lower.endsWith(".mp4") || lower.endsWith(".m4v") -> "video/mp4"
            lower.endsWith(".3gp") -> "video/3gpp"
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") -> "image/jpeg"
            lower.endsWith(".png") -> "image/png"
            else -> null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun compressImage(context: Context, uri: Uri, idSupport: String, currentDate: LocalDateTime): File {
        _isUploadingFile.value = true
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

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

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

        val file = File(
            context.cacheDir,
            "$idSupport ${currentDate.year}-${currentDate.monthValue}-${currentDate.dayOfMonth} ${currentDate.hour}.${currentDate.minute}.${currentDate.second}.jpg"
        )

        val outputStream = FileOutputStream(file)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.close()
        _isUploadingFile.value = false
        return file
    }


    // Se mantiene el uso de Geocoder, pero agrega un manejo explícito de compatibilidad
    @SuppressLint("NewApi")
    private fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Geocoder(context, Locale.getDefault())
            } else {
                Geocoder(context, Locale.getDefault())
            }
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Dirección no encontrada"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener la dirección: ${e.localizedMessage}", e)
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