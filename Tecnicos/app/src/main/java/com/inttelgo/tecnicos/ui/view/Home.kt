package com.inttelgo.tecnicos.ui.view

import com.inttelgo.tecnicos.R
import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.inttelgo.tecnicos.components.ModernTopAppBar
import com.inttelgo.tecnicos.layout.Tareas
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.layout.Proceso
import com.inttelgo.tecnicos.layout.Soporte
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.viewmodel.LoginViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    context: Context,
    navigateToUploadImage: (id: String, type: String) -> Unit,
    navigateToSupport: (idSupport: String) -> Unit,
    navigateToTarea: (idTarea: String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateToProfile: () -> Unit
) {
    val viewModelL: LoginViewModel = remember { LoginViewModel() }
    val viewModelH: HomeViewModel = remember { HomeViewModel() }
    val expanded = remember { mutableStateOf(false) }
    val hasFineLocation = remember { mutableStateOf(false) }
    val hasCoarseLocation = remember { mutableStateOf(false) }
    val userPreferences = UserPreferences(context)
    val hasInternetConnection = rememberNetworkConnectivityState(context)

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
            screen = { Proceso(navigateToUploadImage, context) },
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

    // Verificar si el usuario está logueado
    /*if (userPreferences.getUser() == null) {
        LaunchedEffect(Unit) {
            viewModelL.isLoggedUser(navigateToLogin, userPreferences.getUser() as Nothing?)
        }
    }*/

    // Cargar datos iniciales solo una vez
    LaunchedEffect(Unit) {
        if (userPreferences.getUser() != null && hasInternetConnection.value) {
            viewModelH.consultBarrios()
        }
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
            Column(
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
                ModernTabs(tabs)
            }
        }
    }
}

@Composable
private fun ModernTabs(tabs: List<TabItem>) {
    var selectedTab by remember { mutableIntStateOf(0) }

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
            onItemSelected = { selectedTab = it },
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
            moveTo(x = 0F, y = size.height.toFloat())
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
                        right = size.width.toFloat(),
                        bottom = realRightCornerDiameter
                    ),
                    startAngleDegrees = -90.0f,
                    sweepAngleDegrees = 90.0f,
                    forceMoveTo = false
                )
            }
            // bottom right
            lineTo(x = size.width.toFloat(), y = size.height.toFloat())
            close()
        }
    }
}

// Source - https://stackoverflow.com/a/78329710
// Posted by Jan Itor
// Retrieved 2026-03-19, License - CC BY-SA 4.0

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