@file:Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")

package com.inttelgo.tecnicos.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.parseColor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.logic.Model.Cuenta
import com.inttelgo.tecnicos.logic.Model.Prioridad
import com.inttelgo.tecnicos.logic.Model.PriorityData
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.logic.process.ImageOperations
import com.inttelgo.tecnicos.ui.view.getMediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.core.graphics.toColorInt
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.inttelgo.tecnicos.logic.Model.FotoSoporte
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.logic.Model.FotoInsta
import com.inttelgo.tecnicos.logic.Model.PrioridadType
import com.inttelgo.tecnicos.logic.process.OtherOperarions

@SuppressLint("NewApi")
@Composable
fun PriorityCard(
    prioridad: Prioridad,
    modifier: Modifier = Modifier
) {
    // Verificar si la prioridad es válida
    val hasValidPriority = prioridad.descripcion.isNotBlank() &&
            prioridad.descripcion.lowercase() != "ninguna" &&
            prioridad.color.isNotBlank()

    // Si no hay prioridad válida, mostrar placeholder
    if (!hasValidPriority) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sin prioridad",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        return
    }

    // Obtener los colores predefinidos según el tipo de prioridad
    val prioridadType = PrioridadType.fromString(prioridad.color)

    // Usar colores personalizados si están disponibles, si no, usar los predefinidos
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = prioridadType.backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = prioridadType.borderColor
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = prioridad.descripcion,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = prioridadType.textColor,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
@Composable
fun AlertCard(message: String){
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(10.dp)
            )
            .width(300.dp)
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer,
            ),
            modifier = Modifier.padding(20.dp)
        )
    }
}


@Composable
fun WarningCard(message: String){
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(10.dp)
            )
            .width(300.dp)
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            ),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
fun AnimatedIcon(modifier: Modifier) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = 1000)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
            delay(200)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_mano),
                contentDescription = "logo",
                modifier = Modifier
                    .size(60.dp)
                    .scale(scale.value)
            )
        }
    }
}

@SuppressLint("UseKtx")
@Composable
fun PrioritiesCard(prioritySelected: MutableState<Int>) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        val listPriorities: List<PriorityData> = listOf(
            PriorityData(0,"Todos","#d0e0f5", "#104493", "#b8d2ef"),
            PriorityData(1,"Baja","#d1e7dd", "#105132", "#bbdbcc"),
            PriorityData(2,"Media","#fff4cd","#7a641d", "#ffecb5"),
            PriorityData(3,"Alta", "#f8d7da", "#841f29", "#f5c2c7")
        )
        listPriorities.forEach { p->
            Box(
                modifier = Modifier
                    .background(
                        if (prioritySelected.value == p.id) Color(p.textColor.toColorInt()) else Color(
                            p.backgroundColor.toColorInt()
                        ),
                        RoundedCornerShape(5.dp)
                    )
                    .border(
                        width = 0.dp,
                        color = Color(parseColor(p.borderColor)),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        prioritySelected.value = p.id
                    }
            ) {
                Text(
                    text = p.priorityText,
                    color = if(prioritySelected.value == p.id) Color(parseColor(p.backgroundColor)) else Color(parseColor(p.textColor)),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(5.dp)
                        .background(Color.Transparent)
                )
            }
        }
    }
}

@Composable
fun ConectionBadge(hasInternetConnection: Boolean){
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (hasInternetConnection)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (hasInternetConnection)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    )
            )
            Text(
                text = if (hasInternetConnection) "Online" else "Offline",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (hasInternetConnection)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun CuentaInfoSection(cuenta: Cuenta) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Cuenta #${cuenta.nro_cuenta}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
fun LocationSection(direccion: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_map_pin),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Text(
            text = direccion,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TypeSection(descripcion: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopAppBar(
    userPreferences: UserPreferences,
    expanded: MutableState<Boolean>,
    hasInternetConnection: State<Boolean>,
    navigateToProfile: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo_mano),
                            contentDescription = "Logo Inttelgo",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(8.dp)
                        )
                    }
                    Column {
                        userPreferences.getUser()?.let {
                            Text(
                                text = "${it.name1} ${it.lastname1?.get(0)}.",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            },
            actions = {
                ConectionBadge(hasInternetConnection.value)
                Spacer(Modifier.width(8.dp))

                Surface(
                    onClick = { expanded.value = true },
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_ellipsis_vertical),
                            contentDescription = "Menú",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 8.dp),
                ) {
                    DropdownMenuItem(
                        onClick = { navigateToProfile() },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_circle_user_round),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Mi Perfil",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun formatDate(fecha: String): String {
    return try {
        val instant = Instant.parse(fecha)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()

        // Formato DD/MM/YYYY HH:MM
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
        localDateTime.format(formatter)

    } catch (e: Exception) {
        e.message?.let { Log.e("FormatDate", it) }
        // Fallback: intenta extraer la parte de fecha y hora manualmente
        try {
            val datePart = fecha.substring(0, 10).replace("-", "/")
            val timePart = fecha.substring(11, 16) // Toma HH:MM
            "$datePart $timePart"
        } catch (ex: Exception) {
            "Fecha inválida"
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LazyImages(
    selectedImages: MutableState<List<Uri?>>,
    selectedPreviewUri:  MutableState<Uri?>,
    context: Context,
    isCompressing: MutableState<Boolean>,
    required: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        val isEmpty = selectedImages.value.isEmpty()
        val showError = required && isEmpty

        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Media",
                    tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = if (showError) "Archivos multimedia *" else "Archivos multimedia",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            LazyRow(
                modifier = Modifier.height(120.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CardWithBottomSheet(selectedImages, context, isCompressing, showError)
                }
                items(selectedImages.value) { uri ->
                    val typeM = uri?.let { getMediaType(context, it) }
                    if (typeM?.startsWith("image") == true) {
                        ImagePreview(uri.toString(),
                            onPreview = { selectedPreviewUri.value = uri },
                            onRemove = {
                                selectedImages.value = selectedImages.value.filter { it != uri }
                            })
                    } else if (typeM?.startsWith("video") == true) {
                        VideoPreview(uri, context,
                            onPreview = { selectedPreviewUri.value = uri },
                            onRemove = {
                                selectedImages.value = selectedImages.value.filter { it != uri }
                            })
                    }
                }
            }

            if (selectedImages.value.isEmpty()) {
                Spacer(Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showError)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (showError) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource( if (showError) R.drawable.ic_octagon_alert else R.drawable.ic_info),
                            contentDescription = null,
                            tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = if (showError)
                                "Es obligatorio agregar al menos un archivo"
                            else
                                "Agrega fotos o videos tocando el botón +",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (showError) FontWeight.Medium else FontWeight.Normal
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
                            "${selectedImages.value.size} archivo${if (selectedImages.value.size != 1) "s" else ""} seleccionado${if (selectedImages.value.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            if (showError) {
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CardWithBottomSheet(
    selectedMedia: MutableState<List<Uri?>>,
    context: Context,
    isCompressing: MutableState<Boolean>,
    showError: Boolean
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
            .size(120.dp).padding(12.dp, 0.dp, 0.dp, 0.dp)
            .clickable {
                if (hasPermissions.value) {
                    showBottomSheet.value = true
                } else {
                    Toast.makeText(context, "Por favor, acepta los permisos.", Toast.LENGTH_LONG).show()
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
                        painter = painterResource(if (showError) R.drawable.ic_octagon_alert else R.drawable.ic_plus),
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
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SectionTitle(icon = null, title ="Seleccionar medio")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CameraScreen(selectedMedia, showBottomSheet, context, hasPermissions.value, isCompressing)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            VideoCameraScreen(selectedMedia, showBottomSheet, context, hasPermissions.value, isCompressing)
                        }
                    }

                    MediaSelectorView(selectedMedia, context, hasPermissions.value, isCompressing)

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CameraScreen(
    selectedMedia: MutableState<List<Uri?>>,
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
                            selectedMedia.value += listOf(compressedUri)
                        }
                    } catch (e: Exception) {
                        Log.e("StyledOpenCameraScreen", "Error comprimiendo imagen: ${e.message}")
                        // En caso de error, usar la URI original
                        selectedMedia.value += listOf(photoUriProvider)
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
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun VideoCameraScreen(
    selectedMedia: MutableState<List<Uri?>>,
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
                            selectedMedia.value += listOf(compressedUri)
                        }
                    } catch (e: Exception) {
                        e.message?.let { Log.e("VideoCameraScreen", it) }
                        selectedMedia.value += listOf(videoUriProvider)
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
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MediaSelectorView(
    selectedMedia: MutableState<List<Uri?>>,
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
                        val compressedFiles = mutableListOf<Uri>()
                        // Comprimir cada archivo seleccionado
                        uris.forEachIndexed { index, uri ->
                            try {
                                val currentDate = LocalDateTime.now().plusSeconds(index.toLong())
                                val compressedFile = ImageOperations().uriToFile(context, uri, currentDate)
                                compressedFile?.let {file ->
                                    try {
                                        // Convertir el archivo comprimido de vuelta a Uri
                                        val compressedUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider", // ← .provider NO .fileprovider
                                            file
                                        )
                                        compressedFiles.add(compressedUri)
                                    } catch (e: Exception) {
                                        e.message?.let { Log.e("MediaSelectorView", it) }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("MediaSelector", "Error comprimiendo archivo: ${e.message}")
                            }
                        }
                        selectedMedia.value += compressedFiles

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
                        fontWeight = FontWeight.Bold
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
fun ImagePreview(imageUri: String, onPreview: () -> Unit, onRemove: () -> Unit) {
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
                model = imageUri,
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
fun VideoPreview(videoUri: Uri, context: Context, onPreview: () -> Unit, onRemove: () -> Unit) {
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
                modifier = Modifier.fillMaxWidth().background(Color.White)
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
fun SingleMediaPreview(
    uri: MutableState<Uri?>,
    context: Context,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {

        // Main media content
        uri.value?.let {
            MediaContent(
                uri = it,
                context = context,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 80.dp, horizontal = 16.dp)
            )
        }
        // Top bar with close button and counter
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button
                Surface(
                    onClick = onClose,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Bottom info bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars),
            color = Color.Black.copy(alpha = 0.7f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Media type indicator
                val mediaType = uri.value?.let { getMediaType(context, it) }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (mediaType?.startsWith("video") == true)
                            Icons.Default.PlayArrow
                        else
                            Icons.Default.Favorite,
                        contentDescription = "Tipo de media",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (mediaType?.startsWith("video") == true) "Video" else "Imagen",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }

                val fileName = remember(uri) {
                    try {
                        getFileName(context, uri.value)
                    } catch (e: Exception) {
                        Log.e("Views", e.message ?: "Error getting filename")
                        ""
                    }
                }

                if (fileName.isNotEmpty()) {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(start = 16.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun SingleFotoInstaPreview(
    media: MutableState<FotoInsta?>,
    context: Context,
    onClose: () -> Unit
) {
    BackHandler(enabled = media.value != null) {
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        media.value?.let {
            FotoInstaContent(
                uri = it,
                context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 56.dp)
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onClose,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_x),
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaContent(
    uri: Uri,
    context: Context,
    modifier: Modifier = Modifier
) {
    val mediaType = getMediaType(context, uri)
    val isVideo = mediaType?.startsWith("video") == true

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isVideo) {
            AdvancedVideoPlayer(uri, context)
        } else {
            // Image viewer
            AsyncImage(
                model = uri,
                contentDescription = "Imagen seleccionada",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Transparent)
            )
        }
    }
}

@Composable
private fun FotoInstaContent(
    uri: FotoInsta,
    context: Context,
    modifier: Modifier = Modifier
) {
    val isVideo = uri.link.contains("mp4")

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isVideo) {
            AdvancedVideoLinkPlayer(uri, context)
        } else {
            // Estados del zoom
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            val transformState = rememberTransformableState { zoomChange, panChange, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 5f) // zoom entre 1x y 5x
                offset += panChange * scale
            }

            // Resetear al soltar si el zoom vuelve a 1x
            LaunchedEffect(scale) {
                if (scale == 1f) offset = Offset.Zero
            }

            val painter = rememberAsyncImagePainter(model = uri.link)
            val intrinsicSize = painter.intrinsicSize
            val isSizeSpecified = intrinsicSize.isSpecified && !intrinsicSize.isEmpty()
            val aspectRatio = if (isSizeSpecified) intrinsicSize.width / intrinsicSize.height else 1f
            val isLandscape = isSizeSpecified && intrinsicSize.width > intrinsicSize.height

            Image(
                painter = painter,
                contentDescription = "Imagen seleccionada",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isSizeSpecified) {
                            if (isLandscape) Modifier.aspectRatio(aspectRatio)
                            else Modifier.fillMaxHeight()
                        } else {
                            Modifier.aspectRatio(1f)
                        }
                    )
                    .clip(RoundedCornerShape(12.dp))
                    // 👇 Aplicamos transformaciones de zoom y pan
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
                    .transformable(state = transformState)
            )
        }
    }
}


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
private fun AdvancedVideoPlayer(
    uri: Uri,
    context: Context,

) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    controllerAutoShow = true
                    controllerHideOnTouch = true
                    controllerShowTimeoutMs = 3000
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
private fun AdvancedVideoLinkPlayer(
    uri: FotoInsta,
    context: Context,

    ) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri.link))
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)

    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    controllerAutoShow = true
                    controllerHideOnTouch = true
                    controllerShowTimeoutMs = 3000
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun getFileName(context: Context, uri: Uri?): String {
    return try {
        uri?.let { context.contentResolver.query(it, null, null, null, null) }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex) ?: ""
        } ?: ""
    } catch (e: Exception) {
        e.message?.let { Log.e("getFileName", it) }
        ""
    }
}

@Composable
fun ModernDialog(
    type: DialogType,
    message: String,
    onCancel: () -> Unit,
    onSuccess: () -> Unit,
    title: String? = null,
    cancelText: String = "Cancelar",
    successText: String = "Aceptar"
) {
        Dialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            ),

        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Icono según el tipo
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = type.backgroundColor.copy(alpha = 0.1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = type.icon,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = type.iconColor
                            )
                        }
                    }

                    // Título (opcional)
                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Mensaje
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Botón Cancelar
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = cancelText,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        // Botón Success
                        Button(
                            onClick = onSuccess,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = type.buttonColor
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 2.dp
                            )
                        ) {
                            Text(
                                text = successText,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                )
                            )
                        }
                    }
                }
            }
        }

}

@Composable
fun MediaPreview(evidencias: List<FotoSoporte>, onClose: () -> Unit) {
    val imagenActual = remember { mutableIntStateOf(0) }
    val currentMedia by remember(imagenActual.intValue) {
        derivedStateOf { evidencias.getOrNull(imagenActual.intValue) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 50 && imagenActual.intValue > 0) {
                        imagenActual.intValue--
                    } else if (dragAmount < -50 && imagenActual.intValue < evidencias.size - 1) {
                        imagenActual.intValue++
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Main media content
        currentMedia?.let { media ->
            MediaContent(
                media = media,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 80.dp, horizontal = 16.dp)
            )
        }

        // Top bar with close button and counter
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image counter
                if (evidencias.size > 1) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${imagenActual.intValue + 1} / ${evidencias.size}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                // Close button
                Surface(
                    onClick = onClose,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Navigation arrows (only show if more than one image)
        if (evidencias.size > 1) {
            // Left arrow
            if (imagenActual.intValue > 0) {
                Surface(
                    onClick = { imagenActual.intValue-- },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(16.dp)
                        .size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Anterior",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Right arrow
            if (imagenActual.intValue < evidencias.size - 1) {
                Surface(
                    onClick = { imagenActual.intValue++ },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                        .size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Siguiente",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Bottom info bar
        currentMedia?.let { media ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Media info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date
                        if (media.fecha.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Fecha",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = media.fecha,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                )
                            }
                        }

                        // Location
                        if (media.ubicacion.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Ubicación",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = media.ubicacion,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White.copy(alpha = 0.9f)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Page indicators (dots)
                    if (evidencias.size > 1) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            evidencias.forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(
                                            if (index == imagenActual.intValue) 12.dp else 8.dp
                                        )
                                        .background(
                                            color = if (index == imagenActual.intValue)
                                                Color.White
                                            else
                                                Color.White.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        )
                                        .animateContentSize()
                                        .clickable {
                                            imagenActual.intValue = index
                                        }
                                )
                                if (index < evidencias.size - 1) {
                                    Spacer(Modifier.width(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaContent(
    media: FotoSoporte,
    modifier: Modifier = Modifier
) {
    val isVideo = media.link.endsWith(".mp4", ignoreCase = true) ||
            media.link.endsWith(".mov", ignoreCase = true) ||
            media.link.endsWith(".avi", ignoreCase = true)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isVideo) {
            // Video player
            VideoPlayerComponent(
                videoUrl = media.link,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            // Image viewer
            ImageViewerComponent(
                imageUrl = media.link,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ImageViewerComponent(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val imageKey = remember(imageUrl) { imageUrl.hashCode().toString() }

    var isLoading by remember(imageKey) { mutableStateOf(true) }
    var hasError by remember(imageKey) { mutableStateOf(false) }
    var errorMessage by remember(imageKey) { mutableStateOf("") }
    var loadStarted by remember(imageKey) { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCacheKey(imageKey)
                .diskCacheKey(imageKey)
                .listener(
                    onStart = {
                        if (!loadStarted) {
                            loadStarted = true
                            isLoading = true
                            hasError = false
                        }
                    },
                    onSuccess = { _, result ->
                        isLoading = false
                        hasError = false
                    },
                    onError = { _, error ->
                        isLoading = false
                        hasError = true
                        val throwable = error.throwable
                        errorMessage = throwable.message ?: "Error desconocido"
                    }
                )
                .build(),
            contentDescription = "Evidencia",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )

        // Loading indicator
        if (isLoading) {
            LoadingIndicator()
        }

        // Error state
        if (hasError && !isLoading) {
            ErrorDisplay(errorMessage, imageUrl)
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.3f)
    ) {
        Box(
            modifier = Modifier.padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Cargando imagen...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }
    }
}


@Composable
private fun ErrorDisplay(errorMessage: String, imageUrl: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.3f)
    ) {
        Box(
            modifier = Modifier.padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Error",
                    tint = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Error al cargar imagen",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Yellow.copy(alpha = 0.9f)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "URL: ${imageUrl.takeLast(40)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerComponent(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
        }
    }

    var isPlaying by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                }
            }
        )

        // Play/Pause overlay
        if (!isPlaying) {
            Surface(
                onClick = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                    isPlaying = !isPlaying
                },
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (exoPlayer.isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                        contentDescription = if (exoPlayer.isPlaying) "Pausar" else "Reproducir",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun HistoryToggle(showHistory: MutableState<Boolean>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showHistory.value = !showHistory.value },
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icono indicador de estado
                    Surface(
                        shape = CircleShape,
                        color = if (showHistory.value)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(if (showHistory.value) R.drawable.ic_list_chevrons_up_down else R.drawable.ic_list_down_up),
                                contentDescription = null,
                                tint = if (showHistory.value)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (!showHistory.value) "Ver Historial" else "Ocultar Historial",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Observaciones y seguimiento",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                val rotation by animateFloatAsState(
                    targetValue = if (showHistory.value) 180f else 0f,
                    animationSpec = tween(300, easing = EaseInOutCubic),
                    label = "arrow_rotation"
                )

                Icon(
                    painter = painterResource( if (showHistory.value) R.drawable.ic_chevron_down else R.drawable.ic_chevron_down),
                    contentDescription = if (showHistory.value) "Ocultar historial" else "Mostrar historial",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer { rotationZ = rotation }
                )            }
        }
    }
}
@Composable
fun ObservationBox(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp, max = 120.dp)
                .padding(14.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: Int?,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .widthIn(max = 400.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                icon?.let {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SectionTitle(icon: Int? = null, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun InfoRow(icon: Int?, text: String) {
    if (text.isBlank()) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateChip(label: String, date: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = OtherOperarions().formatFechaBaseDatos(date),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun EmptyHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_circle_off),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Text(
                    text = "Sin historial",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "No hay observaciones registradas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}