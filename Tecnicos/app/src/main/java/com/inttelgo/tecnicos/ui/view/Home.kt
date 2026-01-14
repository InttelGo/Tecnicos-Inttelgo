package com.inttelgo.tecnicos.ui.view

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inttelgo.tecnicos.components.ModernTopAppBar
import com.inttelgo.tecnicos.layout.Tareas
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.layout.Proceso
import com.inttelgo.tecnicos.layout.Soporte
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.viewmodel.LoginViewModel
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
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant // Gray700
        ),
        TabItem(
            title = "Tareas",
            screen = { Tareas(viewModelH, navigateToTarea, context) },
            selectedColor = MaterialTheme.colorScheme.primary, // Orange400
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant // Gray700
        ),
        TabItem(
            title = "Instalacion",
            screen = { Proceso(navigateToUploadImage, context) },
            selectedColor = MaterialTheme.colorScheme.secondary, // DeepOrange500
            unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant // Gray700
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
private fun ModernTabs(tabs: List<com.inttelgo.tecnicos.ui.view.TabItem>) {
    val selectedTab = remember { mutableIntStateOf(0) }
    val animatedIndicatorOffset = animateDpAsState(
        targetValue = (selectedTab.intValue * 120).dp, // Ajustar según el ancho de tab
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "indicator_offset"
    )

    Column {
        // Tabs personalizadas
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, tab ->
                        ModernTab(
                            tab,
                            isSelected = selectedTab.intValue == index,
                            onClick = { selectedTab.intValue = index },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Indicador personalizado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(4.dp)
                            .offset(x = animatedIndicatorOffset.value)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        tabs[selectedTab.intValue].selectedColor,
                                        tabs[selectedTab.intValue].selectedColor.copy(alpha = 0.7f)
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                            )
                    )
                }
            }
        }

        // Contenido del tab seleccionado
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            tabs[selectedTab.intValue].screen()
        }
    }
}

@Composable
private fun ModernTab(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedColor = animateColorAsState(
        targetValue = if (isSelected) tab.selectedColor else tab.unselectedColor,
        animationSpec = tween(300),
        label = "tab_color"
    )

    val animatedScale = animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200),
        label = "tab_scale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .graphicsLayer {
                scaleX = animatedScale.value
                scaleY = animatedScale.value
            },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) {
            animatedColor.value.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Texto del tab
            Text(
                text = tab.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = if (isSelected) 14.sp else 13.sp,
                    color = animatedColor.value
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Clase TabItem actualizada con la nueva paleta
data class TabItem(
    val title: String,
    val icon: ImageVector? = null,
    val screen: @Composable () -> Unit,
    val selectedColor: Color,
    val unselectedColor: Color,
    val selectedGradient: Brush = Brush.horizontalGradient(
        colors = listOf(selectedColor, selectedColor.copy(alpha = 0.7f))
    )
)