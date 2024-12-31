package com.inttelgo.tecnicos.ui.view

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ButtonWithText
import com.inttelgo.tecnicos.logic.ComposeFileProvider
import com.inttelgo.tecnicos.navigation.EnumNavigation

@Preview
@Composable
fun PreviewUploadImg(){
    val navController = rememberNavController()

    NavHost(navController, EnumNavigation.UPLOAD_IMAGE.toString()){
        composable(EnumNavigation.UPLOAD_IMAGE.toString()) {
            UploadImg(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImg(navController: NavController) {
    val selectedImages = remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
    var imageUri = remember {
        mutableStateOf<Uri?>(null)
    }
    var hasImage = remember {
        mutableStateOf(false)
    }
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
                   CardWithBottomSheet(imageUri,selectedImages, hasImage)
               }
                items(selectedImages.value) { uri ->
                    Card(
                        modifier = Modifier.padding(10.dp)
                            .size(80.dp)
                            .clickable(onClick = {  }),
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

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardWithBottomSheet(
    imageUri: MutableState<Uri?>,
    selectedImages: MutableState<List<Uri?>>,
    hasImage: MutableState<Boolean>
) {
    // Estado para controlar la visibilidad del Bottom Sheet
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage.value = success
        }
    )
    val context = LocalContext.current

    // Tarjeta principal
    Card(
        modifier = Modifier
            .padding(10.dp)
            .width(80.dp)
            .height(80.dp)
            .clickable {
                showBottomSheet.value = !showBottomSheet.value
            }
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
                ButtonWithText("Tomar", R.drawable.photo_icon, 40.dp){
                    val uri = ComposeFileProvider.getImageUri(context)
                    imageUri.value = uri
                    Log.d("Photo 1", imageUri.toString())
                    cameraLauncher.launch(uri)
                }
                Spacer(Modifier.width(10.dp))
                //Select image to galery
                PhotoSelectorView(10, selectedImages)

            }
        }
    }
}

@Composable
fun PhotoSelectorView(maxSelectionCount: Int = 10, selectedImages: MutableState<List<Uri?>>, ) {
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImages.value = listOf(uri) }
    )
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = if (maxSelectionCount > 1) {
            maxSelectionCount
        } else {
            2
        }),
        onResult = { uris -> selectedImages.value = uris }
    )

    fun launchPhotoPicker() {
        if (maxSelectionCount > 1) {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
    ButtonWithText("Galeria", R.drawable.image_icon, 40.dp){
        launchPhotoPicker()
    }
}



