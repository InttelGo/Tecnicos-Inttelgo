package com.inttelgo.tecnicos.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
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
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AlertCard
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.InternetAccess
import com.inttelgo.tecnicos.components.NumberField
import com.inttelgo.tecnicos.components.OpenCameraScreen
import com.inttelgo.tecnicos.components.PhotoSelectorView
import com.inttelgo.tecnicos.components.TargetCustom
import com.inttelgo.tecnicos.components.TextFlieldCustom
import com.inttelgo.tecnicos.components.WarningCard
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.ui.viewmodel.UploadImageViewModel

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
@SuppressLint("MissingPermission", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImgScreen(id: String, type: String, context: Context, navigateToHome: () -> Unit, navigateToUp: () -> Unit) {
    val viewModelI: UploadImageViewModel = remember { UploadImageViewModel() }
    val selectedImages = remember { mutableStateOf<List<Uri?>>(emptyList()) }
    val imageSelected = remember { mutableStateOf<Uri?>(null) }
    val observation = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val success = remember { mutableStateOf(false) }
    val elapsedTime = remember { mutableIntStateOf(0) }
    val errorMessage = viewModelI.errorMessage.collectAsState()
    val warningMessage = viewModelI.warningMessage.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val articlesState = remember { mutableStateListOf<Articulo>() }
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
                        CardWithBottomSheet(selectedImages, context)
                    }
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
            item{
                ButtonRainbow("Aceptar", Modifier.fillMaxWidth(), true) {
                    if(hasInternetConnection.value){
                        val userPreferences = UserPreferences(context)
                        userPreferences.getId()?.let {
                            // Llamar a viewModelI.uploadImage con la ubicación
                            viewModelI.uploadImage(context, selectedImages, observation, success, id, type, it, articlesState, elapsedTime)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                ButtonRainbow("Cancelar", Modifier.fillMaxWidth(), false) {
                    navigateToHome()
                }
                ImageAlertDialog(imageSelected, showDialog)
            }
        }
    }
}


@Composable
fun ImageAlertDialog(imageSelected: MutableState<Uri?>, showDialog: MutableState<Boolean>, title: String = "Imágene") {
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
                    model = imageSelected.value,
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            Log.e("Coil", "Error al cargar la imagen: ${state.result.throwable}")
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // Hacer que ocupe todo el ancho
                        .aspectRatio(1f) // Mantener proporción 1:1 (cuadrada)
                ) {
                    Image(
                        painter = painter,
                        contentDescription = "Imagen desde la web",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit // Ajustar imagen sin recortar
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
    context: Context
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
                OpenCameraScreen(selectedImages, showBottomSheet, context)
                Spacer(Modifier.width(10.dp))
                //Select image to galery
                PhotoSelectorView(10, selectedImages, context)
            }
        }
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
    val infiniteTransition = rememberInfiniteTransition()
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
