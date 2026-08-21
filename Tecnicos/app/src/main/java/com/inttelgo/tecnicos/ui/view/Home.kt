package com.inttelgo.tecnicos.ui.view

import com.inttelgo.tecnicos.R
import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.fragment.app.FragmentActivity
import com.inttelgo.tecnicos.components.ModernTopAppBar
import com.inttelgo.tecnicos.components.QrScannerDialog
import com.inttelgo.tecnicos.layout.Tareas
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.layout.Proceso
import com.inttelgo.tecnicos.layout.Soporte
import com.inttelgo.tecnicos.logic.Model.JornadaCheckType
import com.inttelgo.tecnicos.logic.Model.JornadaQrPayload
import com.inttelgo.tecnicos.logic.persistence.BiometricAuth
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.navigation.HomeSection
import com.inttelgo.tecnicos.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.viewmodel.JornadaUiState
import com.inttelgo.tecnicos.viewmodel.JornadaViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    context: Context,
    selectedSection: HomeSection,
    onSectionSelected: (HomeSection) -> Unit,
    navigateToUploadImage: (id: String, type: String) -> Unit,
    navigateToSupport: (idSupport: String) -> Unit,
    navigateToTarea: (idTarea: String) -> Unit,
    navigateToInstalacion: (idInstalacion: String) -> Unit,
    navigateToProfile: () -> Unit,
    jornadaViewModel: JornadaViewModel
) {
    val viewModelH: HomeViewModel = remember { HomeViewModel() }
    val jornadaState by jornadaViewModel.uiState.collectAsState()
    val showIngresoButton by jornadaViewModel.showIngresoButton.collectAsState()
    val showSalidaButton by jornadaViewModel.showSalidaButton.collectAsState()
    val expanded = remember { mutableStateOf(false) }
    val hasFineLocation = remember { mutableStateOf(false) }
    val hasCoarseLocation = remember { mutableStateOf(false) }
    val userPreferences = UserPreferences(context)
    val hasInternetConnection = rememberNetworkConnectivityState(context)

    var pendingCheckType by remember { mutableStateOf<JornadaCheckType?>(null) }
    var showQrScanner by remember { mutableStateOf(false) }
    var scannedQr by remember { mutableStateOf<JornadaQrPayload?>(null) }
    var showOficinaConfirm by remember { mutableStateOf(false) }
    var jornadaFabExpanded by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showQrScanner = true
        } else {
            Toast.makeText(context, "Se requiere la cámara para leer el QR", Toast.LENGTH_LONG).show()
            pendingCheckType = null
        }
    }

    fun startJornadaFlow(type: JornadaCheckType) {
        pendingCheckType = type
        scannedQr = null
        showOficinaConfirm = false
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    fun requestBiometricAndSend(qr: JornadaQrPayload, type: JornadaCheckType) {
        val activity = context as? FragmentActivity
        if (activity == null) {
            Toast.makeText(context, "No se pudo iniciar la biometría", Toast.LENGTH_LONG).show()
            return
        }
        val isIngreso = type == JornadaCheckType.INGRESO
        BiometricAuth.authenticate(
            activity = activity,
            title = if (isIngreso) "Registrar ingreso" else "Registrar salida",
            subtitle = if (isIngreso) {
                "Confirma tu ingreso con la huella"
            } else {
                "Confirma tu salida con la huella"
            },
            onSuccess = {
                jornadaViewModel.registerAfterBiometric(context, type, qr)
            },
            onError = { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            },
            onCancel = {
                Toast.makeText(context, "Debes confirmar con huella para continuar", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Tabs con diseño mejorado usando la nueva paleta
    val tabs = listOf(
        TabItem(
            title = "Soporte",
            screen = { Soporte(viewModelH, navigateToSupport, context) },
            selectedColor = MaterialTheme.colorScheme.primary, // Orange400
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant, // Gray700
            icon = R.drawable.ic_toolbox
        ),
        TabItem(
            title = "Tarea",
            screen = { Tareas(viewModelH, navigateToTarea, context) },
            selectedColor = MaterialTheme.colorScheme.primary, // Orange400
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant, // Gray700
            icon = R.drawable.ic_notebook_text
        ),
        TabItem(
            title = "Instalacion",
            screen = { Proceso(navigateToInstalacion, context) },
            selectedColor = MaterialTheme.colorScheme.secondary, // DeepOrange500
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant, // Gray700
            icon = R.drawable.ic_house_wifi
        )
    )

    // Lanzador para permisos de ubicación
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasFineLocation.value = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        hasCoarseLocation.value = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    }

    LaunchedEffect(Unit) {
        if (!hasFineLocation.value || !hasCoarseLocation.value) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Cargar datos iniciales solo una vez
    LaunchedEffect(Unit) {
        if (userPreferences.getUser() != null && hasInternetConnection.value) {
            viewModelH.consultBarrios()
        }
        // Reintenta cargar jornada si aún no hay id (p. ej. falló el parseo anterior).
        jornadaViewModel.loadJornadaOnce(context)
    }

    LaunchedEffect(showIngresoButton, showSalidaButton) {
        if (!showIngresoButton && !showSalidaButton) {
            jornadaFabExpanded = false
        }
    }

    LaunchedEffect(jornadaState) {
        when (val state = jornadaState) {
            is JornadaUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                pendingCheckType = null
                scannedQr = null
                showOficinaConfirm = false
                jornadaFabExpanded = false
                jornadaViewModel.clearMessage()
            }
            is JornadaUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                jornadaViewModel.clearMessage()
            }
            else -> Unit
        }
    }

    if (showQrScanner && pendingCheckType != null) {
        QrScannerDialog(
            title = if (pendingCheckType == JornadaCheckType.INGRESO) {
                "QR ingreso"
            } else {
                "QR salida"
            },
            onQrScanned = { raw ->
                showQrScanner = false
                val qr = jornadaViewModel.parseJornadaQr(raw)
                if (qr == null) {
                    Toast.makeText(context, "QR inválido", Toast.LENGTH_LONG).show()
                    pendingCheckType = null
                } else {
                    scannedQr = qr
                    showOficinaConfirm = true
                }
            },
            onDismiss = {
                showQrScanner = false
                pendingCheckType = null
            }
        )
    }

    if (showOficinaConfirm && scannedQr != null && pendingCheckType != null) {
        val qr = scannedQr!!
        val type = pendingCheckType!!
        AlertDialog(
            onDismissRequest = {
                showOficinaConfirm = false
                scannedQr = null
                pendingCheckType = null
            },
            title = {
                Text(
                    text = if (type == JornadaCheckType.INGRESO) {
                        "Confirmar ingreso"
                    } else {
                        "Confirmar salida"
                    }
                )
            },
            text = {
                Column {
                    Text("Datos del QR:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = qr.usuario,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID usuario: ${qr.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Oficina: ${qr.oficina.descripcion} (ID ${qr.oficina.id})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Al enviar se solicitará tu huella para registrar la marca.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOficinaConfirm = false
                        requestBiometricAndSend(qr, type)
                    }
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showOficinaConfirm = false
                        scannedQr = null
                        pendingCheckType = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (userPreferences.getUser() == null) {
        // Mostrar loading o redirigir a login
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Scaffold(
            topBar = {
                ModernTopAppBar(
                    userPreferences = userPreferences,
                    expanded = expanded,
                    hasInternetConnection,
                    navigateToProfile
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer // Background color del tema
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
            ) {
                ModernTabs(
                    tabs = tabs,
                    selectedSection = selectedSection,
                    onSectionSelected = onSectionSelected
                )

                if (showIngresoButton || showSalidaButton) {
                    JornadaExpandableFab(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .zIndex(8f)
                            .padding(end = 16.dp, bottom = 88.dp),
                        expanded = jornadaFabExpanded,
                        showIngreso = showIngresoButton,
                        showSalida = showSalidaButton,
                        isLoading = jornadaState is JornadaUiState.Loading,
                        onToggle = { jornadaFabExpanded = !jornadaFabExpanded },
                        onIngresoClick = {
                            jornadaFabExpanded = false
                            startJornadaFlow(JornadaCheckType.INGRESO)
                        },
                        onSalidaClick = {
                            jornadaFabExpanded = false
                            startJornadaFlow(JornadaCheckType.SALIDA)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun JornadaExpandableFab(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    showIngreso: Boolean,
    showSalida: Boolean,
    isLoading: Boolean,
    onToggle: () -> Unit,
    onIngresoClick: () -> Unit,
    onSalidaClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "jornadaFabRotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = expanded && showSalida,
            enter = fadeIn() + scaleIn() + slideInVertically { it / 2 },
            exit = fadeOut() + scaleOut() + slideOutVertically { it / 2 }
        ) {
            JornadaFabAction(
                label = "Salida",
                iconRes = R.drawable.ic_log_out,
                enabled = !isLoading,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                onClick = onSalidaClick
            )
        }

        AnimatedVisibility(
            visible = expanded && showIngreso,
            enter = fadeIn() + scaleIn() + slideInVertically { it / 2 },
            exit = fadeOut() + scaleOut() + slideOutVertically { it / 2 }
        ) {
            JornadaFabAction(
                label = "Ingreso",
                iconRes = R.drawable.ic_camera,
                enabled = !isLoading,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onIngresoClick
            )
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = if (expanded) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentColor = if (expanded) {
                MaterialTheme.colorScheme.onError
            } else {
                MaterialTheme.colorScheme.onPrimary
            },
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(
                    if (expanded) R.drawable.ic_x else R.drawable.ic_calendar_days
                ),
                contentDescription = if (expanded) "Cerrar jornada" else "Jornada",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun JornadaFabAction(
    label: String,
    iconRes: Int,
    enabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
        SmallFloatingActionButton(
            onClick = { if (enabled) onClick() },
            containerColor = containerColor,
            contentColor = contentColor,
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                modifier = Modifier
                    .size(20.dp)
                    .alpha(if (enabled) 1f else 0.5f)
            )
        }
    }
}

private fun HomeSection.toTabIndex(): Int = when (this) {
    HomeSection.Soporte -> 0
    HomeSection.Tareas -> 1
    HomeSection.Procesos -> 2
}

private fun Int.toHomeSection(): HomeSection = when (this) {
    1 -> HomeSection.Tareas
    2 -> HomeSection.Procesos
    else -> HomeSection.Soporte
}

@Composable
private fun ModernTabs(
    tabs: List<TabItem>,
    selectedSection: HomeSection,
    onSectionSelected: (HomeSection) -> Unit
) {
    val selectedTab = selectedSection.toTabIndex()

    val buttons = remember(tabs) {
        tabs.map { tab -> ButtonData(text = tab.title, icon = tab.icon!!) }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            tabs[selectedTab].screen()
        }

        AnimatedNavigationBar(
            buttons = buttons,
            selectedIndex = selectedTab,
            onItemSelected = { onSectionSelected(it.toHomeSection()) },
            barColor = MaterialTheme.colorScheme.surface,
            circleColor = MaterialTheme.colorScheme.primary,
            selectedColor = MaterialTheme.colorScheme.onPrimary,
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AnimatedNavigationBar(
    buttons: List<ButtonData>,
    selectedIndex: Int,                      // ← estado externo
    onItemSelected: (Int) -> Unit,           // ← callback externo
    barColor: Color,
    circleColor: Color,
    selectedColor: Color,
    unselectedColor: Color,
) {
    val circleRadius = 26.dp
    // ← ya no hay "var selectedItem" local, se usa selectedIndex
    var barSize by remember { mutableStateOf(IntSize(0, 0)) }
    val offsetStep = remember(barSize) {
        barSize.width.toFloat() / (buttons.size * 2)
    }
    val offset = remember(selectedIndex, offsetStep) {
        offsetStep + selectedIndex * 2 * offsetStep
    }
    val circleRadiusPx = LocalDensity.current.run { circleRadius.toPx().toInt() }
    val offsetTransition = updateTransition(offset, "offset transition")
    val animation = spring<Float>(dampingRatio = 0.5f, stiffness = Spring.StiffnessVeryLow)
    val cutoutOffset by offsetTransition.animateFloat(
        transitionSpec = {
            if (this.initialState == 0f) snap() else animation
        },
        label = "cutout offset"
    ) { it }
    val circleOffset by offsetTransition.animateIntOffset(
        transitionSpec = {
            if (this.initialState == 0f) snap()
            else spring(animation.dampingRatio, animation.stiffness)
        },
        label = "circle offset"
    ) {
        IntOffset(it.toInt() - circleRadiusPx, -circleRadiusPx)
    }
    val barShape = remember(cutoutOffset) {
        BarShape(offset = cutoutOffset, circleRadius = circleRadius, cornerRadius = 25.dp)
    }

    Box {
        Circle(
            modifier = Modifier
                .offset { circleOffset }
                .zIndex(1f),
            color = circleColor,
            radius = circleRadius,
            button = buttons[selectedIndex],   // ← selectedIndex
            iconColor = selectedColor,
        )
        Row(
            modifier = Modifier
                .onPlaced { barSize = it.size }
                .graphicsLayer { shape = barShape; clip = true }
                .fillMaxWidth()
                .background(barColor),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            buttons.forEachIndexed { index, button ->
                val isSelected = index == selectedIndex  // ← selectedIndex
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onItemSelected(index) },  // ← callback
                    icon = {
                        val iconAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 0f else 1f,
                            label = "Navbar item icon"
                        )
                        Icon(
                            painter = painterResource(id = button.icon),
                            contentDescription = button.text,
                            modifier = Modifier.alpha(iconAlpha)
                        )
                    },
                    label = { Text(button.text) },
                    colors = NavigationBarItemDefaults.colors().copy(
                        selectedIconColor = unselectedColor,
                        selectedTextColor = unselectedColor,
                        unselectedIconColor = unselectedColor,
                        unselectedTextColor = unselectedColor,
                        selectedIndicatorColor = Color.Transparent,
                    )
                )
            }
        }
    }
}

// Clase TabItem actualizada con la nueva paleta
data class TabItem(
    val title: String,
    val icon: Int? = null,
    val screen: @Composable () -> Unit,
    val selectedColor: Color,
    val unselectedColor: Color,
    val selectedGradient: Brush = Brush.horizontalGradient(
        colors = listOf(selectedColor, selectedColor.copy(alpha = 0.7f))
    )
)

private class BarShape(
    private val offset: Float,
    private val circleRadius: Dp,
    private val cornerRadius: Dp,
    private val circleGap: Dp = 5.dp,
) : Shape {

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(getPath(size, density))
    }

    private fun getPath(size: androidx.compose.ui.geometry.Size, density: Density): Path {
        val cutoutCenterX = offset
        val cutoutRadius = density.run { (circleRadius + circleGap).toPx() }
        val cornerRadiusPx = density.run { cornerRadius.toPx() }
        val cornerDiameter = cornerRadiusPx * 2
        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 1.5f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            // bottom left
            moveTo(x = 0F, y = size.height)
            // top left
            if (cutoutLeftX > 0) {
                val realLeftCornerDiameter = if (cutoutLeftX >= cornerRadiusPx) {
                    // there is a space between rounded corner and cutout
                    cornerDiameter
                } else {
                    // rounded corner and cutout overlap
                    cutoutLeftX * 2
                }
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = 0f,
                        right = realLeftCornerDiameter,
                        bottom = realLeftCornerDiameter
                    ),
                    startAngleDegrees = 180.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            lineTo(cutoutLeftX, 0f)
            // cutout
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )
            // top right
            if (cutoutRightX < size.width) {
                val realRightCornerDiameter = if (cutoutRightX <= size.width - cornerRadiusPx) {
                    cornerDiameter
                } else {
                    (size.width - cutoutRightX) * 2
                }
                arcTo(
                    rect = Rect(
                        left = size.width - realRightCornerDiameter,
                        top = 0f,
                        right = size.width,
                        bottom = realRightCornerDiameter
                    ),
                    startAngleDegrees = -90.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            // bottom right
            lineTo(x = size.width, y = size.height)
            close()
        }
    }
}
@Composable
private fun Circle(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    radius: Dp,
    button: ButtonData,
    iconColor: Color,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(radius * 2)
            .clip(CircleShape)
            .background(color),
    ) {
        AnimatedContent(
            targetState = button.icon, label = "Bottom bar circle icon",
        ) { targetIcon ->
            Icon(
                painter = painterResource(id = targetIcon),
                contentDescription = button.text,
                tint = iconColor
            )
        }
    }
}

data class ButtonData(val text: String, val icon: Int)