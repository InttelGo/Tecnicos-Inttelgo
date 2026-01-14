package com.inttelgo.tecnicos.layout

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.CuentaInfoSection
import com.inttelgo.tecnicos.components.LocationSection
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.ObservationSection
import com.inttelgo.tecnicos.components.PrioritiesCard
import com.inttelgo.tecnicos.components.PriorityCard
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.Model.Tarea
import com.inttelgo.tecnicos.logic.process.homeProcess
import com.inttelgo.tecnicos.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.viewmodel.TareaViewModel
import kotlin.collections.component1
import kotlin.collections.component2

@SuppressLint("DiscouragedApi")
@Composable
fun Tareas(
    viewModelH: HomeViewModel,
    navigateToTarea: (idTarea: String) -> Unit,
    context: Context
){
    val viewModel: TareaViewModel = remember { TareaViewModel() }
    val filters = remember { mutableStateListOf(Filter("id_tarea", "contains", ""), Filter("id_estado_solicitud", "equals", "1", logic = "AND"), Filter("id_estado_solicitud", "equals", "2", logic = "OR")) }
    val sorting = remember { Sorting("fecha_creacion", true) }
    val prioritySelected = remember { mutableIntStateOf(0) }
    val tareas by viewModel.tareasData.collectAsState()
    val barrios by viewModelH.barrios.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)

    // Content Section
    if(hasInternetConnection.value){
        LaunchedEffect(prioritySelected.intValue) {
            viewModel.consultMoreTareas(filters= filters, sorting= sorting )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            // Header Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Title with connection indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PrioritiesCard(prioritySelected)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Estados de carga mejorados
            when {
                // Sin conexión
                !hasInternetConnection.value -> {
                    EmptyStateCard(
                        icon = Icons.Default.Search,
                        title = "Sin conexión",
                        message = "Por favor verifica tu conexión a internet",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Carga inicial (primera página y sin datos previos)
                isLoading && currentPage == 1 && tareas.isNullOrEmpty() -> {
                    AnimatedIcon(Modifier.fillMaxSize(), "Cargando tareas...")
                }

                // Sin tareas después de cargar
                !isLoading && tareas.isNullOrEmpty() -> {
                    EmptyStateCard(
                        icon = Icons.Default.Close,
                        title = "No hay tareas",
                        message = "No se encontraron tareas asignadas",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Cargando barrios
                barrios.isNullOrEmpty() -> {
                    AnimatedIcon(Modifier.fillMaxSize(), "Cargando información...")
                }

                // Mostrar contenido
                else -> {
                    val ticketsArreglados = tareas?.let {
                        barrios?.let { it2 -> homeProcess().generarConjuntoTareas(it, it2) }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        ticketsArreglados?.forEach { (barrio, tareasPorBarrio) ->
                            if (tareasPorBarrio.isNotEmpty()) {
                                item {
                                    BarrioHeader(barrio = barrio, tareaCount = tareasPorBarrio.size)
                                }
                                items(
                                    items = tareasPorBarrio,
                                    key = { it.id_tarea }
                                ) { tarea ->
                                    ModernTareaCard(
                                        tarea = tarea,
                                        onViewMore = {
                                            navigateToTarea(tarea.id_tarea.toString())
                                        }
                                    )
                                }
                                item {
                                    LoadMoreTrigger(viewModel, filters, sorting)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de error
        errorMessage?.let {
            ModernDialog(
                type = DialogType.ERROR,
                message = it,
                title = "Error",
                onCancel = {
                    viewModel.clearMessages()
                },
                onSuccess = {
                    viewModel.clearMessages()
                },
                cancelText = "Cerrar",
                successText = "Reintentar"
            )
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
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
private fun BarrioHeader(barrio: String, tareaCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Barrio: $barrio",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = "$tareaCount tarea${if (tareaCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun ModernTareaCard(
    tarea: Tarea,
    onViewMore: () -> Unit
) {
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
                .padding(20.dp)
        ) {
            // Ticket Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Tarea #${tarea.id_tarea}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                tarea.prioridad?.let { PriorityCard(it) }
            }

            Spacer(Modifier.height(20.dp))

            // Main Content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Client Information
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tarea.cuenta?.let {
                        CuentaInfoSection(cuenta = it)
                        LocationSection(direccion = it.direccion)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            ObservationSection(tarea.observacion)
            Spacer(Modifier.height(20.dp))

            // Action Button
            Button(
                onClick = onViewMore,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ver detalles",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadMoreTrigger(
    viewModel: TareaViewModel,
    filters: List<Filter>,
    sorting: Sorting
) {
    val isLoading by viewModel.loading.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        when {
            // Mostrar indicador de carga
            isLoading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Cargando más tareas...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Mostrar indicador de fin de lista
            currentPage > totalPages -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "No hay más tareas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Trigger para cargar más
            currentPage <= totalPages -> {
                LaunchedEffect(Unit) {
                    viewModel.consultMoreTareas(
                        filters = filters,
                        sorting = sorting
                    )
                }
            }
        }
    }
}