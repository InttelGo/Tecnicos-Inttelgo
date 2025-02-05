package com.inttelgo.tecnicos.ui.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AlertCard
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.ButtonWithText
import com.inttelgo.tecnicos.components.InternetAccess
import com.inttelgo.tecnicos.components.NumberField
import com.inttelgo.tecnicos.components.TargetCustom
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
import java.time.LocalDateTime

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
    val success = remember { mutableStateOf(false) }
    val elapsedTime = remember { mutableIntStateOf(0) }
    val errorMessage = viewModelI.errorMessage.collectAsState()
    val warningMessage = viewModelI.warningMessage.collectAsState()
    val uploadedImagesList = viewModelI.uploadedImagesList.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val uploadProgress = remember { mutableFloatStateOf(0f) }
    val articlesState = remember { mutableStateListOf<Articulo>() }
    val imagesState = viewModelI.uploadImageState.collectAsState()
    if(type == "Proceso"){
        LaunchedEffect (Unit){
            viewModelI.getImages(id)
        }
    }
    CircularCountUpTimer(elapsedTime)
    AnimatedSuccessAlert(success, navigateToUp)
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
        if(imagesState.value){
            success.value = true
        }
        if (uploadProgress.floatValue > 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                com.inttelgo.tecnicos.components.AnimatedIcon()
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(20.dp)
                    .fillMaxSize()
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
                        if(type=="Proceso"){
                            uploadedImagesList.value?.let { list ->
                                items(list) { picture ->
                                    Card(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxHeight()
                                            .clickable(onClick = {
                                                showDialog.value = true
                                                imageSelected.value = picture?.foto.toString()
                                            }),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        AsyncImage(
                                            model = picture?.foto, // La URL de la imagen
                                            contentDescription = "Imagen cargada",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit,
                                            onError = { error ->
                                                Log.e("Coil", "Error al cargar la imagen: $error")
                                            }
                                        )
                                    }
                                }
                            }
                        }else{
                            items(selectedImages.value) { uri ->
                                Card(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxHeight()
                                        .clickable(onClick = {
                                            showDialog.value = true
                                            imageSelected.value = uri
                                        }),
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = CardDefaults.cardElevation(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
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
                            ProcessForm(tipoI.value!!.descripcion, articlesState)
                        }
                    }
                }
                when(type){
                    "Proceso" -> textButton.value = "Finalizar Proceso"
                    "Finalizar" -> textButton.value = "Finalizar soporte"
                    "Soporte" -> textButton.value = "Agregar Observacion"
                }
                item{
                    ButtonRainbow(textButton.value, Modifier.fillMaxWidth(), true) {
                        if(hasInternetConnection.value){
                            val userPreferences = UserPreferences(context)
                                userPreferences.getId()?.let {
                                    // Llamar a viewModelI.uploadImage con la ubicación
                                    viewModelI.uploadImages(
                                        context,
                                        selectedImages,
                                        observation,
                                        id,
                                        type,
                                        it,
                                        articlesState
                                    )
                                }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    ButtonRainbow("Cancelar", Modifier.fillMaxWidth(), false) {
                        viewModelH.ActualizarEstadoI(id, 7, "",null){
                                _: String, _: String ->
                        }
                        navigateToHome()
                    }
                    ImageAlertDialog(
                        imageSelected = imageSelected,
                        showDialog = showDialog,
                        title = "Imagen"
                    )

                }
            }
        }
    }
}


@Composable
fun ImageAlertDialog(
    imageSelected: MutableState<Any?>, // Acepta Uri o String (URL)
    showDialog: MutableState<Boolean>,
    title: String = "Imagen"
) {
    if (showDialog.value) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            },
            text = {
                val painter = rememberAsyncImagePainter(
                    model = when (val image = imageSelected.value) {
                        is Uri -> image
                        is String -> image
                        else -> null
                    },
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            Log.e("Coil", "Error al cargar la imagen: ${state.result.throwable}")
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Imagen cargada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = "Cerrar")
                }
            }
        )
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardWithBottomSheet(
    selectedImages: MutableState<List<Uri?>>,
    context: Context,
    type: String,
    viewModelI: UploadImageViewModel,
    id: String
) {
    // Estado para controlar la visibilidad del Bottom Sheet
    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }

    // Tarjeta principal
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight()
            .width(60.dp)
            .clickable {
                showBottomSheet.value = !showBottomSheet.value
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box (
            modifier = Modifier
               .padding(10.dp)
               .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Icon(
                painter = painterResource(R.drawable.add_image_icon),
                contentDescription = "Imagen Icon",
                modifier = Modifier
                    .size(50.dp),
                tint = Color.Black
            )
        }
    }

    // Bottom Sheet
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false // Cierra el Bottom Sheet al tocar fuera
            },
            sheetState = sheetState
        ) {
            // Contenido del Bottom Sheet
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                //Take a picture
                OpenCameraScreen(selectedImages, showBottomSheet, context,type, viewModelI, id)
                Spacer(Modifier.width(10.dp))
                //Select image to galery
                PhotoSelectorView(10, selectedImages, context, type, viewModelI, id)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OpenCameraScreen(
    photoUri: MutableState<List<Uri?>>,
    showBottomSheet: MutableState<Boolean>,
    context: Context,
    type: String,
    viewModelI: UploadImageViewModel,
    id: String
) {
    val hasCameraPermission = remember { mutableStateOf(false) }

    val cameraPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission.value = isGranted
    }

    val photoFile = remember { File(context.cacheDir, "${LocalDateTime.now()}.jpg") }
    // Uri para el archivo (utilizando FileProvider)
    val photoUriProvider = FileProvider.getUriForFile( context, "${context.packageName}.provider", photoFile)

    // Lanzador de la cámara
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                if(type == "Proceso"){
                    val userPreferences = UserPreferences(context)
                    userPreferences.getId()?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModelI.generateImage(
                                context = context,
                                imagesUpload = mutableStateOf(listOf(photoUriProvider)),
                                idTicket = id,
                                idObs = 0,
                                type = type,
                                idTec = it,
                            )
                        }
                    }
                }else{
                    photoUri.value += listOf(photoUriProvider)
                }
                showBottomSheet.value = false
            }
        }
    )
    // Solicitar permisos de medios según la versión
    LaunchedEffect(Unit) {
        if (!hasCameraPermission.value) {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }
    // Solicitar permiso de cámara si no está otorgado
    ButtonWithText("Tomar Foto", R.drawable.photo_icon, 40.dp) {
        if(!hasCameraPermission.value){
            Log.d("PhotoSelector", "Permissions are not granted.")
            Toast.makeText(context, "Por favor, acepta los permisos para acceder a la camara.", Toast.LENGTH_LONG).show()
        }else{
            launcher.launch(photoUriProvider)
        }
    }
}

@Composable
private fun PhotoSelectorView(
    maxSelectionCount: Int = 10,
    selectedImages: MutableState<List<Uri?>>,
    context: Context,
    type: String,
    viewModelI: UploadImageViewModel,
    id: String
) {
    val hasMediaPermissions = remember { mutableStateOf(false) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImages.value += listOf(uri) }
    )

    val mediaPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasMediaPermissions.value = permissions.values.all { it }
        Log.d("PhotoSelector", "Permissions granted: ${hasMediaPermissions.value}")
    }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = if (maxSelectionCount > 1) {
            maxSelectionCount
        } else {
            2
        }),
        onResult = { uris ->
            if(type == "Proceso"){
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
                        )
                    }
                }
            }else{
                selectedImages.value += uris
            }
        }
    )

    fun launchPhotoPicker() {
        if (hasMediaPermissions.value) {
            if (maxSelectionCount > 1) {
                multiplePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            } else {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        } else {
            Log.d("PhotoSelector", "Permissions are not granted.")
            Toast.makeText(context, "Por favor, acepta los permisos para acceder a la galería.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasMediaPermissions.value) {
            val permissions = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                }
                else -> {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
            Log.d("PhotoSelector", "Requesting permissions...")
            mediaPermissionRequest.launch(permissions)
        }
    }

    ButtonWithText("Galería", R.drawable.image_icon, 40.dp) {
        Log.d("PhotoSelector", "Button clicked, attempting to launch picker.")
        launchPhotoPicker()
    }
}


@Composable
private fun AnimatedSuccessAlert(showDialog: MutableState<Boolean>, navigateToUp: () -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showDialog.value = false },
            title = { Text("Observacion creada!") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedIcon()
                    Spacer(Modifier.height(10.dp))
                    Text("Tu transaccion ha sido un exito.")
                }
            },
            confirmButton = {
                TargetCustom("Aceptar", true) {
                    showDialog.value = false
                    navigateToUp()
                }
            }
        )
    }
}

@Composable
private fun AnimatedIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        ), label = ""
    )
    Icon(
        painter = painterResource(id = R.drawable.verified_icon),
        contentDescription = "Success",
        modifier = Modifier.size(70.dp).scale(scale),
        tint = Color.Green
    )
}

@Composable
private fun ProcessForm(
    type: String,
    articles: SnapshotStateList<Articulo>
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
                        modifier = Modifier.width(screenWidth * 0.55f)
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

@SuppressLint("UnrememberedMutableState", "DefaultLocale")
@Composable
fun CircularCountUpTimer(elapsedTime: MutableState<Int>) {
    val isRunning = remember { mutableStateOf(true) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    // Inicializar el cronómetro automáticamente al inicio
    LaunchedEffect(Unit) {
        if (isRunning.value) {
            val totalTimeInSeconds = 76800
            timer = object : CountDownTimer(
                totalTimeInSeconds * 1000L,  // Tiempo total
                1000
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    elapsedTime.value += 1 // Incrementa el tiempo transcurrido en segundos
                    if (elapsedTime.value >= totalTimeInSeconds) {
                        onFinish() // Forzar la finalización cuando se alcance el tiempo límite
                    }
                }
                override fun onFinish() {
                    TODO("Not yet implemented")
                }
            }
            timer?.start()
        }
    }
}


