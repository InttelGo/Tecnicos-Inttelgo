package com.inttelgo.tecnicos.components

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.inttelgo.tecnicos.R
import java.io.File
import java.time.LocalDateTime

@Composable
fun TextFlieldCustom (title: String, content: MutableState<String>, w: Dp ){
    OutlinedTextField(
        value = content.value,
        shape = RoundedCornerShape(10.dp),
        onValueChange = { content.value = it },
        label = { Text(title) },
        modifier = Modifier.width(w)
    )
}

@Composable
fun PassFlied(password: MutableState<String>, title: String,w: Dp) {
    val flag = remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),
        value = password.value,
        modifier = Modifier.width(w),
        onValueChange = { password.value = it },
        label = { Text(title) },
        visualTransformation = if (flag.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (flag.value)
                R.drawable.visibility_off_icon
           else R.drawable.visibility_icon

            IconButton(onClick = { flag.value = !flag.value }) {
                Icon(
                    painter = painterResource(image) ,
                    tint = Color.Black,
                    contentDescription = if (flag.value) "Hide password" else "Show password"
                )
            }
        }
    )
}

@Composable
fun ButtonRainbow(text: String, modifier: Modifier, onClick: () -> Unit){
    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFff9900),
                            Color(0xFFff6700)
                        )
                    )
                )
                .clip(RoundedCornerShape(10.dp))
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFffffff)
                )
            )
        }
    }
}

@Composable
fun SearchInput(search: MutableState<String>, onClick: () -> Unit){

    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),
        value = search.value,
        modifier = Modifier.width(350.dp),
        onValueChange = { search.value = it },
        label = { Text("Buscar") },
        trailingIcon = {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = "Search icon",
                    tint = Color.Black, // Aplica un color base al ícono
                    modifier = Modifier.fillMaxSize() // Asegúrate de que el ícono ocupe todo el espacio del contenedor
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OpenCameraScreen(photoUri: MutableState<List<Uri?>>) {
    val context = LocalContext.current
    val photoFile = remember { File(context.cacheDir, "${LocalDateTime.now()}.jpg") }
    // Uri para el archivo (utilizando FileProvider)
    val photoUriProvider = FileProvider.getUriForFile( context, "${context.packageName}.provider", photoFile)
    // Estado para el permiso de cámara
    val cameraPermissionState = remember { mutableStateOf(false) }

    // Lanzador de la cámara
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                photoUri.value += listOf(photoUriProvider)
            }
        }
    )

    // Lanzador para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermissionState.value = isGranted
        }
    )

    // Solicitar permiso de cámara si no está otorgado
    ButtonWithText("Tomar Foto", R.drawable.photo_icon, 40.dp) {
        if(!cameraPermissionState.value){
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }else{
            launcher.launch(photoUriProvider)
        }
    }
}

@Composable
fun PhotoSelectorView(
    maxSelectionCount: Int = 10,
    selectedImages: MutableState<List<Uri?>>) {
    val galleryPermissionState = remember { mutableStateOf(false) }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImages.value += listOf(uri) }
    )
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = if (maxSelectionCount > 1) {
            maxSelectionCount
        } else {
            2
        }),
        onResult = { uris -> selectedImages.value += uris }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        galleryPermissionState.value = isGranted
    }

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
        if(galleryPermissionState.value){
            launchPhotoPicker()
        }else{
            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            launchPhotoPicker()
        }

    }
}

@Composable
fun TextButtonForm(text: String, isPrimary: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .then(
                if (isPrimary) {
                    Modifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                } else Modifier // No fondo para el secundario
            )
            .border(
                width = if (isPrimary) 0.dp else 0.dp,
                color = if (isPrimary) Color.White else Color.Black,
                shape = RoundedCornerShape(50.dp)
            )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if(isPrimary)Color.White else Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(12.dp)
        )
    }
}