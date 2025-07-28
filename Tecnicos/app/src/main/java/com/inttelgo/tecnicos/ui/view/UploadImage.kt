package com.inttelgo.tecnicos.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AlertCard
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.ButtonWithText
import com.inttelgo.tecnicos.components.InternetAccess
import com.inttelgo.tecnicos.components.NumberField
import com.inttelgo.tecnicos.components.TextFlieldCustom
import com.inttelgo.tecnicos.components.WarningCard
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.ui.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.ui.viewmodel.UploadImageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
fun PreviewUpladImage(){
    val context = LocalContext.current
    val navigateToHome = { /* TODO: Implement navigation to HomeScreen */ }
    val navigateToUp = { /* TODO: Implement navigation to UploadImageScreen */ }
    UploadImgScreen(id = "12345", type = "Proceso", context = context, navigateToHome = navigateToHome, navigateToUp = navigateToUp)
}

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("MissingPermission", "DefaultLocale", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImgScreen(id: String, type: String, context: Context, navigateToHome: () -> Unit, navigateToUp: () -> Unit) {
    val viewModelI: UploadImageViewModel = remember { UploadImageViewModel() }
    val viewModelH: HomeViewModel = remember { HomeViewModel() }
    val selectedImages = remember { mutableStateOf<List<Uri?>>(emptyList()) }
    val imageSelected = remember { mutableStateOf<Any?>(null) }
    val observation = remember { mutableStateOf("") }
    val textButton = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val errorMessage = viewModelI.errorMessage.collectAsState()
    val warningMessage = viewModelI.warningMessage.collectAsState()
    val uploadedImagesList = viewModelI.uploadedImagesList.collectAsState()
    val isUploadingFile = viewModelI.isUploadingFile.collectAsState()
    val uploadImageState = viewModelI.uploadImageState.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val uploadProgress = remember { mutableFloatStateOf(0f) }
    val articlesState = remember { mutableStateListOf<Articulo>() }
    val isUploading = isUploadingFile.value
    val showPreview = remember { mutableStateOf(false) }
    val previewUri = remember { mutableStateOf<String?>(null) }
    if(type == "Proceso"){
        LaunchedEffect (Unit){
            viewModelI.getImages(id)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.logo_inttelgo_2),
                        contentDescription = null,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(15.dp)
                    )
                },
                actions = {
                    Text(
                        "$type : $id",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(15.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        if (uploadProgress.floatValue > 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                com.inttelgo.tecnicos.components.AnimatedIcon()
            }
        }else{

            if (isUploadingFile.value) {
                Log.d("VideoCompression", "Mostrando animación de carga")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)), // Fondo gris semitransparente
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            color = Color.White,
                            strokeWidth = 6.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Subiendo archivos...",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(20.dp)
                    .fillMaxSize(),
                userScrollEnabled = !isUploading
            ) {
                //Conexion
                item {
                    if(!hasInternetConnection.value){
                        InternetAccess(hasInternetConnection.value)
                    }
                }
                item {
                    errorMessage.value?.let { AlertCard(it) }
                    warningMessage.value?.let { WarningCard(it) }
                }
                item {
                    //Images
                    Text("Imágenes")
                    LazyRow(
                        Modifier.height(100.dp)
                    ) {
                        item {
                            CardWithBottomSheet(selectedImages, context, type, viewModelI, id)
                        }
                        items(selectedImages.value) { uri ->
                            val typeM = uri?.let { getMediaType(context, it) }
                            if (typeM?.startsWith("image") == true) {
                                ImagePreview(uri.toString()) {
                                    showPreview.value = true
                                    previewUri.value = uri.toString()
                                }
                            } else if (typeM?.startsWith("video") == true) {
                                VideoPreviewCard(uri, context) {
                                    showPreview.value = true
                                    previewUri.value = uri.toString()
                                }
                            }
                        }
                        if (type == "Proceso") {
                            uploadedImagesList.value?.let { list ->
                                items(list) { picture ->
                                    val imageUrl = picture?.foto ?: ""
                                    val isVideo = imageUrl.endsWith(".mp4", ignoreCase = true)

                                    Card(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxHeight()
                                            .clickable {
                                                showPreview.value = true
                                                previewUri.value = imageUrl
                                            },
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        if (isVideo) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .background(Color.Black),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Video",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(50.dp)
                                                )
                                            }
                                        } else {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = "Imagen cargada",
                                                modifier = Modifier.fillMaxHeight(),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item{
                    TextFlieldCustom("Observación", observation, 350.dp)
                    Spacer(Modifier.height(10.dp))
                    //Process articles
                    if(type == "Proceso"){
                        LaunchedEffect (Unit){
                            viewModelI.getTypeI(id)
                        }
                        val tipoI = viewModelI.tipoI.collectAsState()
                        val checkTipoI = viewModelI.checkTipoI.collectAsState()
                        val checkArticles = viewModelI.checkArticles.collectAsState()
                        val articles = viewModelI.articles.collectAsState()
                        if(checkTipoI.value){
                            LaunchedEffect (Unit){
                                viewModelI.getArticles(tipoI.value!!.id_plan)
                            }
                        }
                        if(checkArticles.value){
                            articlesState.clear() // Limpia los datos anteriores
                            articlesState.addAll(articles.value!!) // Agrega los nuevos artículos
                            ProcessForm(tipoI.value!!.descripcion, articlesState, !isUploading)
                        }
                    }
                }
                when(type){
                    "Proceso" -> textButton.value = "Finalizar Proceso"
                    "Finalizar" -> textButton.value = "Finalizar soporte"
                    "Soporte" -> textButton.value = "Agregar Observacion"
                }
                item{
                    ButtonRainbow(textButton.value, Modifier.fillMaxWidth(), true, !isUploading) {
                        if(hasInternetConnection.value){
                            val userPreferences = UserPreferences(context)
                            userPreferences.getId()?.let {
                                Log.d("UsuarioId", it)
                                // Llamar a viewModelI.uploadImage con la ubicación
                                viewModelI.uploadImages(
                                    context,
                                    selectedImages,
                                    observation,
                                    id,
                                    type,
                                    it,
                                    articlesState
                                ){
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    ButtonRainbow("Cancelar", Modifier.fillMaxWidth(), false, !isUploading) {
                        viewModelH.ActualizarEstadoI(id, 7, "",null){
                                _: String, _: String ->
                        }
                        navigateToHome()
                    }
                }
            }
            if (isUploadingFile.value) {
                Log.d("VideoCompression", "Mostrando animación de carga")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)), // Fondo gris semitransparente
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            color = Color.White,
                            strokeWidth = 6.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Subiendo archivos...",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if(uploadImageState.value ){
                Log.d("VideoCompression", "Mostrando animación de confirmacion")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)), // Fondo gris semitransparente
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SuccessLottieAnimation(uploadImageState.value, navigateToUp, type)

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "$type, Ha sido creada con exito",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (showPreview.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .clickable { showPreview.value = false }, // Cerrar al hacer clic en cualquier parte
                    contentAlignment = Alignment.Center
                ) {
                    previewUri.value?.let { uri ->
                        val isVideo = uri.endsWith(".mp4", ignoreCase = true)

                        if (isVideo) {
                            VideoPlayer(uri, type, context)
                        } else {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Previsualización de imagen",
                                modifier = Modifier.fillMaxWidth().height(400.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getMediaType(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)
}

@Composable
fun SuccessLottieAnimation(isSuccess: Boolean, navigateToUp: () -> Unit, type: String) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_check))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isSuccess,
        restartOnPlay = false
    )

    // Detecta el final de la animación y navega
    LaunchedEffect(progress) {
        if (progress == 1f ) {
            navigateToUp()
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(100.dp)
    )
}
@Composable
fun ImagePreview(imageUri: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Imagen seleccionada",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun VideoPreviewCard(videoUri: Uri, context: Context, onClick: () -> Unit) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(50.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.width(50.dp)
        )
    }
}

@Composable
fun VideoPlayer(videoUri: String, type: String, context: Context) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = if (type == "Soporte") {
                MediaItem.fromUri(videoUri)
            } else {
                MediaItem.fromUri(Uri.parse(videoUri))
            }
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
            }
        },
        modifier = Modifier.fillMaxWidth().height(400.dp)
    )
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardWithBottomSheet(
    selectedMedia: MutableState<List<Uri?>>,
    context: Context,
    type: String,
    viewModelI: UploadImageViewModel,
    id: String
) {
    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }
    val hasPermissions = remember { mutableStateOf(false) }

    val permissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        }
        else -> {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    val permissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantedPermissions ->
        hasPermissions.value = grantedPermissions.values.all { it }
    }

    LaunchedEffect(Unit) {
        permissionRequest.launch(permissions)
    }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight()
            .width(60.dp)
            .clickable {
                if (hasPermissions.value) {
                    showBottomSheet.value = !showBottomSheet.value
                } else {
                    Toast.makeText(context, "Por favor, acepta los permisos.", Toast.LENGTH_LONG).show()
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.padding(10.dp).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.add_image_icon),
                contentDescription = "Imagen Icon",
                modifier = Modifier.size(50.dp),
                tint = Color.Black
            )
        }
    }

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {

                        OpenCameraScreen(selectedMedia, showBottomSheet, context, type, viewModelI, id, hasPermissions.value)
                    Spacer(Modifier.width(10.dp))

                        OpenVideoCameraScreen(selectedMedia, showBottomSheet, context, type, viewModelI, id, hasPermissions.value)

                }
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                        MediaSelectorView(10, selectedMedia, context, type, viewModelI, id, hasPermissions.value)
                }
            }
        }
    }
}

@Composable
private fun OpenCameraScreen(
    selectedMedia: MutableState<List<Uri?>>,
    showBottomSheet: MutableState<Boolean>,
    context: Context,
    type: String,
    viewModelI: UploadImageViewModel,
    id: String,
    enabled: Boolean
) {
    val photoFile = remember { File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg") }
    val photoUriProvider = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                if (type == "Proceso") {
                    val userPreferences = UserPreferences(context)
                    userPreferences.getId()?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModelI.generateImage(
                                context = context,
                                imagesUpload = mutableStateOf(listOf(photoUriProvider)),
                                idTicket = id,
                                idObs = 0,
                                type = type,
                                idTec = it
                            ) {}
                        }
                    }
                } else {
                    selectedMedia.value += listOf(photoUriProvider)
                }
                showBottomSheet.value = false
            }
        }
    )
    ButtonWithText("Tomar Foto", R.drawable.photo_icon, 40.dp, enabled) {
        launcher.launch(photoUriProvider)
    }
}


@Composable
private fun OpenVideoCameraScreen(
    selectedMedia: MutableState<List<Uri?>>,
    showBottomSheet: MutableState<Boolean>,
    context: Context,
    type: String,
    viewModelI: UploadImageViewModel,
    id: String,
    enabled: Boolean
) {
    val videoFile = remember { File(context.cacheDir, "video_${System.currentTimeMillis()}.mp4") }
    val videoUriProvider = FileProvider.getUriForFile(context, "${context.packageName}.provider", videoFile)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            if (success) {
                if (type == "Proceso") {
                    val userPreferences = UserPreferences(context)
                    userPreferences.getId()?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModelI.generateImage(
                                context = context,
                                imagesUpload = mutableStateOf(listOf(videoUriProvider)),
                                idTicket = id,
                                idObs = 0,
                                type = type,
                                idTec = it
                            ) {}
                        }
                    }
                } else {
                    selectedMedia.value += listOf(videoUriProvider)
                }
                showBottomSheet.value = false
            }
        }
    )
    ButtonWithText("Grabar Video", R.drawable.video_icon, 40.dp, enabled) {
        launcher.launch(videoUriProvider)
    }
}

@Composable
private fun MediaSelectorView(
    maxSelectionCount: Int = 10,
    selectedMedia: MutableState<List<Uri?>>,
    context: Context,
    type: String,
    viewModelI: UploadImageViewModel,
    id: String,
    enabled: Boolean
) {
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxSelectionCount),
        onResult = { uris ->
            if (type == "Proceso") {
                val userPreferences = UserPreferences(context)
                userPreferences.getId()?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModelI.generateImage(
                            context = context,
                            imagesUpload = mutableStateOf(uris),
                            idTicket = id,
                            idObs = 0,
                            type = type,
                            idTec = it
                        ) {}
                    }
                }
            } else {
                selectedMedia.value += uris
            }
        }
    )

    ButtonWithText("Seleccionar Foto/Video", R.drawable.image_icon, 40.dp, enabled ) {
        mediaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

}

@Composable
private fun ProcessForm(
    type: String,
    articles: SnapshotStateList<Articulo>,
    enabled: Boolean
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val proportionalHeight = screenHeight * 0.5f

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .height(proportionalHeight)
    ) {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Tipo de instalacion: $type")
            }
        }

        items(articles) { articulo ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        articulo.descripcion,
                        Modifier.width(screenWidth * 0.45f),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    NumberField(
                        number = articulo.cantidad,
                        label = "Cantidad" + if(articulo.id_articulo.toInt() in 109..111)  "( Opcional )" else "",
                        modifier = Modifier.width(screenWidth * 0.55f),
                        enabled = enabled
                    ) { newCantidad ->
                        val index = articles.indexOf(articulo)
                        if (index != -1) {
                            articles[index] = articulo.copy(cantidad = newCantidad)
                        }
                    }
                }
            }
        }
    }
}
