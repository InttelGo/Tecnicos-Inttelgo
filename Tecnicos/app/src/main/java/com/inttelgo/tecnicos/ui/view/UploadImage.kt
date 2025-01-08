package com.inttelgo.tecnicos.ui.view

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.OpenCameraScreen
import com.inttelgo.tecnicos.components.PhotoSelectorView
import com.inttelgo.tecnicos.components.TextFlieldCustom
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.ui.viewmodel.UploadImageViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImgScreen(id: String, type: String, context: Context,navigateToHome: () -> Unit, navigateToUp: () -> Unit) {
    val viewModelI:UploadImageViewModel = remember { UploadImageViewModel() }
    val selectedImages = remember { mutableStateOf<List<Uri?>>(emptyList()) }
    val imageSelected = remember { mutableStateOf<Uri?>(null)}
    val observation = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val success = remember { mutableStateOf(false)}
    AnimatedSuccessAlert(success, navigateToUp)
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
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Text("Imagenes")
            LazyRow {
               item {
                   CardWithBottomSheet(selectedImages)
               }
                items(selectedImages.value) { uri ->
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(80.dp)
                            .clickable(onClick = {
                                showDialog.value = true
                               imageSelected.value = uri
                            }),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ){
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                }
            }

            TextFlieldCustom("Observacion", observation, 350.dp)
            Spacer(Modifier.height(50.dp))
            ButtonRainbow("Aceptar", Modifier.fillMaxWidth()) {
                if (selectedImages.value.isNotEmpty()) {
                    val userPreferences = UserPreferences(context)
                    userPreferences.getId()?.let {
                        viewModelI.uploadImage(context, selectedImages, observation, success, id, type,
                            it
                        )
                    }
                }
            }
            Spacer(Modifier.height(25.dp))
            ButtonRainbow("Cancelar", Modifier.fillMaxWidth()) {
                navigateToHome()
            }
            ImageAlertDialog(imageSelected, showDialog)
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
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                ) {
                    item{
                        Box(
                            modifier = Modifier
                                .width(350.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                        ) {
                            AsyncImage(
                                model = imageSelected.value,
                                contentDescription = "Imagen",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
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
) {
    // Estado para controlar la visibilidad del Bottom Sheet
    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }

    // Tarjeta principal
    Card(
        modifier = Modifier
            .padding(10.dp)
            .width(80.dp)
            .height(80.dp)
            .clickable {
                showBottomSheet.value = !showBottomSheet.value
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Icon(
            painter = painterResource(R.drawable.add_image_icon),
            contentDescription = "Imagen Icon",
            modifier = Modifier
                .padding(10.dp)
                .size(60.dp),
            tint = Color.Black
        )
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
                OpenCameraScreen(selectedImages)
                Spacer(Modifier.width(10.dp))
                //Select image to galery
                PhotoSelectorView(10, selectedImages)
            }
        }
    }
}

@Composable
fun AnimatedSuccessAlert(showDialog: MutableState<Boolean>, navigateToUp: () -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showDialog.value = false },
            title = { Text("Observacion creada con exito!") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedIcon()
                    Text("Tu transaccion ha sido un exito.")
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    navigateToUp()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
fun AnimatedIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        ), label = ""
    )

    Icon(
        painter = painterResource(id = R.drawable.check_small_icon),
        contentDescription = "Success",
        modifier = Modifier.size(70.dp).scale(scale),
        tint = Color.Green
    )
}
