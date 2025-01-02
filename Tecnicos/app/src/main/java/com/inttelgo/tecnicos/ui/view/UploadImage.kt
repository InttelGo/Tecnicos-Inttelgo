package com.inttelgo.tecnicos.ui.view

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ButtonRainbow
import com.inttelgo.tecnicos.components.ButtonWithText
import com.inttelgo.tecnicos.components.ImagePreview
import com.inttelgo.tecnicos.components.OpenCameraScreen
import com.inttelgo.tecnicos.components.PhotoSelectorView
import com.inttelgo.tecnicos.components.TextFlieldCustom

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImgScreen(id: String, type: String, navigateToHome: () -> Unit) {
    val selectedImages = remember { mutableStateOf<List<Uri?>>(emptyList()) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageSelected = remember { mutableStateOf<Uri?>(null)}
    val observation = remember { mutableStateOf("") }
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
                        "${type} : ${id}",
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
                   CardWithBottomSheet(imageUri,selectedImages)
               }
                item {
                    if(imageUri.value != null){
                        imageUri.value.let { uri ->
                            Card(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(80.dp)
                                    .clickable {
                                        imageSelected.value = uri
                                    },
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Imagen capturada",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
                items(selectedImages.value) { uri ->
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(80.dp)
                            .clickable(onClick = {
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
                    if(imageSelected.value != null){
                        ImagePreview(imageSelected)
                    }

                }
           }
            TextFlieldCustom("Observacion", observation, 350.dp)
            Spacer(Modifier.height(50.dp))
            ButtonRainbow("Aceptar", Modifier.fillMaxWidth()) {
                navigateToHome()
            }
            Spacer(Modifier.height(25.dp))
            ButtonRainbow("Cancelar", Modifier.fillMaxWidth()) {
                navigateToHome()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardWithBottomSheet(
    imageUri: MutableState<Uri?>,
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
                OpenCameraScreen(imageUri)
                Spacer(Modifier.width(10.dp))
                //Select image to galery
                PhotoSelectorView(10, selectedImages)
            }
        }
    }
}




