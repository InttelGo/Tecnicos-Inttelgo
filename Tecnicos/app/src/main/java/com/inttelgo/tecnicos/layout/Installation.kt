package com.inttelgo.tecnicos.layout

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.inttelgo.tecnicos.components.CustomButton
import com.inttelgo.tecnicos.components.TextArea
import com.inttelgo.tecnicos.logic.Model.FotoInsta
import com.inttelgo.tecnicos.logic.process.ImageOperations
import com.inttelgo.tecnicos.viewmodel.ProcesoViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import com.github.gcacace.signaturepad.views.SignaturePad
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.NumberField
import com.inttelgo.tecnicos.components.SectionTitle
import com.inttelgo.tecnicos.logic.Model.DialogType

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Installation (
    id: String,
    context: Context,
    navigateToHome: ()-> Unit,
    navigateToUp: () -> Unit,
    modifier: Modifier,
    selectedPreviewFotoInsta:  MutableState<FotoInsta?>,
    isCompressing: MutableState<Boolean>
){
    val viewModel: ProcesoViewModel = remember { ProcesoViewModel() }
    val isUploadingFile by viewModel.isUploadingFile.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val warningMessage by viewModel.warningMessage.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()
    val observacion =  remember { mutableStateOf("") }
    val isLoading by viewModel.retrofitLoading.collectAsState()
    val successMessage by viewModel.successFinishMessage.collectAsState()
    val isFormValid =  selectedImages?.isNotEmpty() == true
    val showSignatureDialog = remember { mutableStateOf(false) }
    val signatureBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val articulosData by viewModel.articlesData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.consultEvicencias(id)
        viewModel.consultArticulos(id)
    }

    if (showSignatureDialog.value) {
        SignatureDialog(
            onConfirm = { bitmap ->
                signatureBitmap.value = bitmap
                showSignatureDialog.value = false
                viewModel.finish(id, observacion, signatureBitmap.value, context)
            },
            onCancel = {
                // Limpiar el dibujo y cerrar diálogo
                signatureBitmap.value = null
                showSignatureDialog.value = false
            }
        )
    }

    LazyColumn (
        modifier = modifier.padding(vertical = 16.dp)){
        item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SectionTitle(icon = R.drawable.ic_file, if (!isFormValid) "Archivos multimedia *" else "Archivos multimedia")

                            // Media row
                            LazyRow(
                                modifier = Modifier.height(120.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    CardWithBottomSheet(id, viewModel, context, isCompressing,
                                        selectedImages?.isEmpty() == true
                                    )
                                }
                                selectedImages?.let {
                                    items(it) { uri ->
                                        if (uri.link.contains("mp4")) {
                                            VideoPreview(uri, context,
                                                onPreview = {
                                                    selectedPreviewFotoInsta.value = uri
                                                },
                                                onRemove = {
                                                    viewModel.removeMedia(uri)
                                                })
                                        } else {
                                            ImagePreview(uri,
                                                onPreview = {
                                                    selectedPreviewFotoInsta.value = uri
                                                },
                                                onRemove = {
                                                    viewModel.removeMedia(uri)
                                                }
                                            )
                                        }
                                    }
                                }

                            }

                            // Info section
                            if (selectedImages?.isEmpty() == true) {
                                Spacer(Modifier.height(16.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (!isFormValid)
                                            MaterialTheme.colorScheme.errorContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = if (!isFormValid) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(if (!isFormValid) R.drawable.ic_octagon_alert else R.drawable.ic_info),
                                            contentDescription = null,
                                            tint = if (!isFormValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text = if (!isFormValid)
                                                "Es obligatorio agregar al menos un archivo"
                                            else
                                                "Agrega fotos o videos tocando el botón +",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (!isFormValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (!isFormValid) FontWeight.Medium else FontWeight.Normal
                                            )
                                        )
                                    }
                                }
                            } else {
                                Spacer(Modifier.height(16.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            "${selectedImages?.size} archivo${if (selectedImages?.size != 1) "s" else ""} seleccionado${if (selectedImages?.size != 1) "s" else ""}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }

                            if (!isFormValid) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Debe agregar al menos una foto o video",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
        item{
            Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            TextArea(
                                value = observacion.value,
                                onValueChange = { observacion.value = it },
                                label = "Observacion",
                                placeholder = "",
                                required = true
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
        articulosData?.let { articulos ->
            item {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Artículos",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        articulos.forEach { articulo ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = articulo.nombre,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                NumberField (
                                    number = articulo.cantidad,
                                    label = "Cantidad",
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = true,
                                    showButtons = true,
                                    minValue = 0,
                                    maxValue = Int.MAX_VALUE,
                                    required = false,
                                    onChange = { nuevaCantidad ->
                                        viewModel.updateArticuloCantidad(articulo.id, nuevaCantidad)
                                    }
                                )

                                if (articulo != articulos.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
        item {
            CustomButton(
                isLoading,
                disabled = !isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                title = "Subir Datos",
                chargeTitle = "Subiendo...",
                disabledTitle = "Campos vacíos",
                ) {
                showSignatureDialog.value = true
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    successMessage?.let {
        ModernDialog(
            type = DialogType.SUCCESS,
            message = it,
            title = "¡Éxito!",
            onCancel = {
                viewModel.clearMessages() // Limpiar mensaje
                navigateToUp()
            },
            onSuccess = {
                viewModel.clearMessages()
                navigateToUp()
            },
            cancelText = "Cerrar",
            successText = "Continuar"
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardWithBottomSheet(
    id: String,
    viewModel: ProcesoViewModel,
    context: Context,
    isCompressing: MutableState<Boolean>,
    showError: Boolean
){
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

    // Styled add button
    Card(
        modifier = Modifier
            .size(120.dp).padding(12.dp, 0.dp, 0.dp, 0.dp)
            .clickable {
                if (hasPermissions.value) {
                    showBottomSheet.value = true
                } else {
                    Toast.makeText(context, "Por favor, acepta los permisos.", Toast.LENGTH_LONG)
                        .show()
                }
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(2.dp, if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (showError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource( if (showError) R.drawable.ic_octagon_alert else R.drawable.ic_plus),
                        contentDescription = if (showError) "Requerido" else "Agregar",
                        tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(Alignment.Center)
                    )
                }
                Text(
                    "Agregar",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet.value = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color =  Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Header
                        SectionTitle(icon = null, title = "Añadir Evidencia")


                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                CameraScreen(id, viewModel, showBottomSheet, context, hasPermissions.value, isCompressing)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                VideoCameraScreen(id, viewModel, showBottomSheet, context, hasPermissions.value, isCompressing)
                            }
                        }
                        MediaSelectorView(id, viewModel, context, hasPermissions.value, isCompressing)

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CameraScreen(
    id: String,
    viewModel: ProcesoViewModel,
    showBottomSheet: MutableState<Boolean>,
    context: Context,
    enabled: Boolean,
    isCompressing: MutableState<Boolean>
) {
    val photoFile = remember { File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg") }
    val photoUriProvider = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)

    val coroutineScope = rememberCoroutineScope ()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                isCompressing.value = true
                coroutineScope.launch {
                    try {
                        val compressedFile = ImageOperations().uriToFile(
                            context = context,
                            uri = photoUriProvider,
                            currentDate = LocalDateTime.now()
                        )
                        compressedFile?.let { file ->
                            val compressedUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            viewModel.createEvidencia(id, compressedUri, context)
                        }
                    } catch (e: Exception) {
                        Log.e("StyledOpenCameraScreen", "Error comprimiendo imagen: ${e.message}")
                        viewModel.createEvidencia(id, photoUriProvider, context)
                    }finally {
                        isCompressing.value = false
                    }
                }
                showBottomSheet.value = false
            }
        }
    )

    Card(
        onClick = {
            launcher.launch(photoUriProvider)
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "Cámara",
                    tint = Color(0xFFFFA726),
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
            Text(
                "Tomar Foto",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
            Text(
                "Usar la cámara",
                style = MaterialTheme.typography.bodySmall.copy(
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun VideoCameraScreen(
    id: String,
    viewModel: ProcesoViewModel,
    showBottomSheet: MutableState<Boolean>,
    context: Context,
    enabled: Boolean,
    isCompressing: MutableState<Boolean>
) {
    val videoFile = remember { File(context.cacheDir, "video_${System.currentTimeMillis()}.mp4") }
    val videoUriProvider = FileProvider.getUriForFile(context, "${context.packageName}.provider", videoFile)
    val coroutineScope = rememberCoroutineScope ()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            if (success) {
                isCompressing.value = true
                coroutineScope.launch {
                    try {
                        val compressedFile = ImageOperations().uriToFile(
                            context = context,
                            uri = videoUriProvider,
                            currentDate = LocalDateTime.now()
                        )
                        compressedFile?.let { file ->
                            val compressedUri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            viewModel.createEvidencia(id, compressedUri, context)
                        }
                    } catch (e: Exception) {
                        e.message?.let { Log.e("VideoCameraScreen", it) }
                        viewModel.createEvidencia(id, videoUriProvider, context)
                    }finally {
                        isCompressing.value = false
                        showBottomSheet.value = false
                    }
                }
            }
        }
    )

    Card(
        onClick = {
            launcher.launch(videoUriProvider)
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_video),
                    contentDescription = "Video",
                    tint = Color(0xFFFF5722),
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
            Text(
                "Grabar Video",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
            Text(
                "Grabar con cámara",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MediaSelectorView(
    id: String,
    viewModel: ProcesoViewModel,
    context: Context,
    enabled: Boolean,
    isCompressing: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val maxSelectionCount = 10

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxSelectionCount),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                // Iniciar proceso de compresión
                isCompressing.value=true
                coroutineScope.launch {
                    try {
                        val currentDate = LocalDateTime.now()

                        // Comprimir cada archivo seleccionado
                        uris.forEach { uri ->
                            try {
                                val compressedFile = ImageOperations().uriToFile(context, uri, currentDate)
                                compressedFile?.let {file ->
                                    try {
                                        // Convertir el archivo comprimido de vuelta a Uri
                                        val compressedUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            file
                                        )
                                        viewModel.createEvidencia(id, compressedUri, context)
                                    } catch (e: Exception) {
                                        e.message?.let { Log.e("MediaSelectorView", it) }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("MediaSelector", "Error comprimiendo archivo: ${e.message}")
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("MediaSelector", "Error en proceso de compresión: ${e.message}")
                    } finally {
                        isCompressing.value=false
                    }
                }
            }
        }
    )

    Card(
        onClick = {
            mediaPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_images),
                    contentDescription = "Galería",
                    tint = Color(0xFF666666),
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Seleccionar de Galería",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF666666)
                    )
                )
                Text(
                    "Hasta $maxSelectionCount archivos",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun VideoPreview(video: FotoInsta, context: Context, onPreview: () -> Unit, onRemove: () -> Unit) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(video.link))
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
            .size(120.dp)
            .clickable(onClick = onPreview),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Play overlay
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reproducir",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }

            // Remove button
            Surface(
                onClick = onRemove,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(24.dp) // Tamaño del botón circular
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImagePreview(image: FotoInsta, onPreview: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onPreview),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = image.link,
                contentDescription = "Imagen seleccionada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Remove button
            Surface(
                onClick = onRemove,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// Composable para el diálogo de firma
@Composable
fun SignatureDialog(
    onConfirm: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    var signatureBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var signaturePadView by remember { mutableStateOf<SignaturePad?>(null) }
    var isValid by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Firma del Comprobante",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Área de firma usando AndroidView
                AndroidView(
                    factory = { context ->
                        SignaturePad(context, null).apply {
                            setOnSignedListener(object : SignaturePad.OnSignedListener {

                                override fun onStartSigning() {
                                    isValid = false
                                }

                                override fun onSigned() {
                                    signatureBitmap = signatureBitmap
                                }

                                override fun onClear() {
                                    signatureBitmap = null
                                    isValid = true
                                }
                            })
                            setMinWidth(2f)
                            setMaxWidth(4f)
                            setVelocityFilterWeight(0.9f)
                            signaturePadView = this
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para limpiar firma
                TextButton(
                    onClick = {
                        signaturePadView?.clear()
                        signatureBitmap = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Log.d("Draw", "firmando.... ${signaturePadView?.isEmpty}")
                    CustomButton(
                        isLoading = false,
                        disabled = isValid,
                        title = "Confirmar",
                        chargeTitle = "Procesando...",
                        disabledTitle = "Firma requerida",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            signaturePadView?.let { pad ->
                                if (!pad.isEmpty) {
                                    val bitmap = pad.signatureBitmap
                                    onConfirm(bitmap)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}