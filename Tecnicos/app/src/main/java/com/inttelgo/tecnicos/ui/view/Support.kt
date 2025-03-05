package com.inttelgo.tecnicos.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AlertCard
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.FloatingButtons
import com.inttelgo.tecnicos.components.InternetAccess
import com.inttelgo.tecnicos.components.PhoneCard
import com.inttelgo.tecnicos.components.WarningCard
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.Picture
import com.inttelgo.tecnicos.ui.viewmodel.SupportViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(idSuport: String, context: Context,navigateToUploadImage: (id: String, type: String) -> Unit){
    val viewModelS: SupportViewModel = remember { SupportViewModel() }
    val showHistory = remember { mutableStateOf(false) }
    val support by viewModelS.supportData.collectAsState()
    val supportcheck by viewModelS.supportCheck.collectAsState()
    val pictureList by viewModelS.pictureList.collectAsState()
    val picturesCheck by viewModelS.picturesCheck.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.logo_inttelgo_2),
                        contentDescription = null,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(15.dp),
                    )
                },
                actions = {
                    Text(
                        "Ticket: $idSuport",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(15.dp)
                    )
                }
            )

        },
        floatingActionButton = {
            FloatingButtons(idSuport, navigateToUploadImage)
        }
    ){ innerPadding ->

            Column (
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val hasInternetConnection = rememberNetworkConnectivityState(context)
                if(!hasInternetConnection.value){
                    InternetAccess(hasInternetConnection.value)
                }else{
                    LaunchedEffect (Unit) {
                        viewModelS.getSupportData(idSuport)
                    }
                }
                if(!supportcheck){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedIcon()
                    }
                }else{
                Card (
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ){
                    Column (
                        modifier = Modifier.padding(20.dp)
                    ){
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "#Cliente: ${support!!.cliente.nroCliente}",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.DarkGray
                            ),
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Cliente: ${support!!.cliente.nombre_1} ${support!!.cliente.apellido_1}",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            ),
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Numeros Telefonicos: ",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        )
                        Spacer(Modifier.height(5.dp))
                        PhoneCard(support!!.cliente.telefono_1)
                        Spacer(Modifier.height(5.dp))
                        support!!.cliente.telefono_2?.let { PhoneCard(it) }
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "Direccion: ${support!!.cliente.direccion}",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.DarkGray
                            )
                        )
                        Spacer(Modifier.height(5.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(
                        Modifier
                            .height(1.dp)
                            .width(120.dp)
                            .background(Color.Black))
                    Card(
                        onClick = {showHistory.value = !showHistory.value},
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ){
                        Row (
                            Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                            Text(if(!showHistory.value) "Ver Historial" else "Ver menos")
                            Spacer(Modifier.width(10.dp))
                            Icon(
                                painter = painterResource(if(!showHistory.value) R.drawable.arrow_drop_down_icon else R.drawable.arrow_drop_upward_icon),
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                    Spacer(
                        Modifier
                            .height(1.dp)
                            .width(120.dp)
                            .background(Color.Black))
                }

                if(showHistory.value){
                    ListObs(viewModelS, idSuport, context, showDialog)
                }
            }
        }
        if(showDialog.value){
            MediaPreview(pictureList){
                showDialog.value = false
            }
        }
    }
}


@Composable
fun ListObs(
    viewModelS: SupportViewModel,
    idSuport: String,
    context: Context,
    showDialog: MutableState<Boolean>
){
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    if(hasInternetConnection.value){
        LaunchedEffect(Unit) {
            viewModelS.getObs(idSuport)
        }
    }
    val listCheck by viewModelS.listCheck.collectAsState()
    val observationList by viewModelS.observationList.collectAsState()
    val errorMessage by viewModelS.errorMessage.collectAsState()
    val warningMessage by viewModelS.warningMessage.collectAsState()
    if(!listCheck){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedIcon()
        }
    }else{

            LazyColumn (
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                item {
                    errorMessage?.let { AlertCard(it) }
                    warningMessage?.let { WarningCard(it) }
                }
                items(observationList!!) { obs ->
                    Spacer(Modifier.height(10.dp))
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ){
                        Row (Modifier.padding(20.dp)){
                            Column (
                                Modifier.width(250.dp)
                            ){
                                Text(
                                    "Observacion",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    obs.obs,
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.height(10.dp))
                                Row {
                                    Text(
                                        obs.fecha,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        obs.tecnico.nombre+" "+obs.tecnico.apellido,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                }
                            }
                            //Pick Icon
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    "Evidencia",
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(5.dp))
                                IconButton(onClick = {
                                    viewModelS.getImgs(obs.id_obs_ticket)
                                    showDialog.value = true
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.photo_library_icon),
                                        contentDescription = "Photo_library icon",
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

    }
}

@Composable
fun MediaPreview(imageUrls: List<Picture>?, onClose: () -> Unit) {
    val totalImages = imageUrls?.size ?: 0
    val imagenActual = remember { mutableIntStateOf(0) }
    var currentUrl by remember { mutableStateOf(imageUrls?.getOrNull(imagenActual.intValue)?.foto) }

    // Actualizar la URL cuando cambia la imagen
    LaunchedEffect(imagenActual.intValue) {
        currentUrl = imageUrls?.getOrNull(imagenActual.intValue)?.foto
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 0 && imagenActual.intValue > 0) {
                        imagenActual.intValue--
                    } else if (dragAmount < 0 && imagenActual.intValue < totalImages - 1) {
                        imagenActual.intValue++
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { onClose() }, // Cerrar al hacer clic en cualquier parte
            contentAlignment = Alignment.Center
        ) {
                    currentUrl?.let { url ->
                        if (url.endsWith(".mp4", ignoreCase = true)) {
                            VideoPlayer(videoUrl = url)
                        } else {
                            Image(
                                painter = rememberAsyncImagePainter(url),
                                contentDescription = "Imagen desde la web",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(9f / 16f),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
        }
    }
}


@Composable
fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f), // Formato estándar de video
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}