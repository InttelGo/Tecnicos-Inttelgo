package com.inttelgo.tecnicos.layout

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.EmptyStateCard
import com.inttelgo.tecnicos.components.InfoRow
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.ObservationBox
import com.inttelgo.tecnicos.components.PhoneCard
import com.inttelgo.tecnicos.components.SectionTitle
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.Proceso
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.process.OtherOperarions
import com.inttelgo.tecnicos.viewmodel.ProcesoViewModel

@Composable
fun Proceso(
    navigateToUploadImage: (id: String, type: String) -> Unit,
    context: Context
) {
    val viewModel: ProcesoViewModel = remember { ProcesoViewModel() }
    val procesos by viewModel.procesosData.collectAsState()
    val checkProcessData by viewModel.checkProcessData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingInstalacion by viewModel.isLoadingInstalacion.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val filters = remember { mutableStateListOf(Filter("id_instalacion", "contains", "")) }
    val sorting = remember { listOf(Sorting("id_instalacion", true)) }
    val search = remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)

    LaunchedEffect(search.value) {
        if (hasInternetConnection.value) {
            val updatedFilters = filters.map { filter ->
                if (filter.column == "id_instalacion") {
                    filter.copy(
                        value = search.value,
                        operator = if (search.value.length > 3) "equals" else "contains"
                    )
                } else {
                    filter
                }
            }
            filters.clear()
            filters.addAll(updatedFilters)

            viewModel.resetPagination()
            viewModel.consultMoreProcess(filters, 10, sorting)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            // Header con título y estadísticas
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Campo de búsqueda estilo Facebook
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            BasicTextField(
                                value = search.value,
                                onValueChange = { search.value = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                decorationBox = { innerTextField ->
                                    if (search.value.isEmpty()) {
                                        Text(
                                            text = "Buscar instalación...",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            // Botón limpiar (solo visible si hay texto)
                            AnimatedVisibility(visible = search.value.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Limpiar",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { search.value = "" },
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Contenido principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                when {
                    // Sin conexión
                    !hasInternetConnection.value -> {
                        EmptyStateCard(
                            icon = R.drawable.ic_octagon_alert,
                            title = "Sin conexión",
                            message = "Por favor verifica tu conexión a internet",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Carga inicial
                    !checkProcessData && isLoading && currentPage == 1 -> {
                        AnimatedIcon(Modifier.fillMaxSize())
                    }

                    // Sin procesos después de cargar
                    checkProcessData && procesos.isNullOrEmpty() -> {
                        EmptyState(
                            searchQuery = search.value,
                            onRefresh = {
                                viewModel.resetPagination()
                                viewModel.consultMoreProcess(filters, 10, sorting)
                            }
                        )
                    }

                    // Mostrar lista
                    checkProcessData && !procesos.isNullOrEmpty() -> {
                        ProcessList(
                            procesos = procesos!!,
                            onInitProcess = { proceso ->
                                viewModel.ActualizarEstadoI(proceso, navigateToUploadImage)
                            },
                            onLoadMore = {
                                LoadMoreTrigger(viewModel, filters, sorting)
                            }
                        )
                    }
                }
            }
        }

        // Overlay de carga cuando se inicia un proceso
        if (isLoadingInstalacion) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Iniciando proceso...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Por favor espera",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Diálogo de éxito
    successMessage?.let {
        ModernDialog(
            type = DialogType.SUCCESS,
            message = it,
            title = "¡Éxito!",
            onCancel = {
                viewModel.clearMessages()
            },
            onSuccess = {
                viewModel.clearMessages()
            },
            cancelText = "Cerrar",
            successText = "Continuar"
        )
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

@Composable
private fun EmptyState(
    searchQuery: String,
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Text(
                text = if (searchQuery.isEmpty()) {
                    "No hay procesos disponibles"
                } else {
                    "No se encontraron procesos"
                },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = if (searchQuery.isEmpty()) {
                    "Cuando tengas procesos de instalación, aparecerán aquí"
                } else {
                    "Intenta con otro término de búsqueda"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRefresh,
                modifier = Modifier.padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Actualizar",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ProcessList(
    procesos: List<Proceso>,
    onInitProcess: (Proceso) -> Unit,
    onLoadMore: @Composable () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(procesos) { proceso ->
            CardProceso(proceso) {
                onInitProcess(it)
            }
        }

        item {
            onLoadMore()
        }
    }
}

@Composable
private fun CardProceso(proceso: Proceso, onInitProcess: (id: Proceso) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Header: ID, Identificación y Estado ──────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "ID: ${proceso.id?.toString() ?: "N/A"}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (proceso.estado?.id == 7)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = if (proceso.estado?.id == 7) "NUEVO" else "EN PROCESO",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (proceso.estado?.id == 7)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // ── Nombre ───────────────────────────────────────────────────────
            Text(
                text = proceso.nombre?.takeIf { it.isNotBlank() } ?: "Nombre no disponible",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            proceso.identificacion?.takeIf { it.isNotBlank() }?.let { id ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "# identificacion "+id,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Log.d("ProcesoView", proceso.toString())
            // ── Fechas ───────────────────────────────────────────────────────
            proceso.fecha_i?.let {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(title = "Fecha Instalacion")
                InfoRow(icon= R.drawable.ic_calendar_days, text = it )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ── Teléfonos ────────────────────────────────────────────────────
            if (!proceso.telefonos.isNullOrBlank()) {
                SectionTitle(icon = R.drawable.ic_phone,title = "Contactos")
                val telefonos = try {
                    separarNumerosTelefonicos(proceso.telefonos)
                } catch (e: Exception) {
                    emptyList()
                }

                if (telefonos.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(telefonos) { numero -> PhoneCard(numero) }
                    }
                } else {
                    Text(
                        text = proceso.telefonos,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            // ── Dirección y Barrio ───────────────────────────────────────────
            SectionTitle(icon = R.drawable.ic_map_pin, "Ubicacion")
            Column (verticalArrangement = Arrangement.spacedBy(8.dp) ){
                proceso.direccion?.let {InfoRow(icon = null, text = "Dirección: ${it}")}
                proceso.barrio?.descripcion?.let { InfoRow(icon = null, text = "Barrio: $it") }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            // ── Plan ─────────────────────────────────────────────────────────
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Plan",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Text(
                        text = buildString {
                            proceso.plan?.let {append( "${it.id} MB " )}
                            proceso.tipo_plan?.let { append(it.descripcion) }
                            append(" ")
                            append(proceso.tipo_servicio?.descripcion)
                        },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }



            // ── Observación ──────────────────────────────────────────────────
            proceso.observacion?.let { it -> ObservationBox(it.descripcion) }

            // ── Usuarios ─────────────────────────────────────────────────────
            proceso.usuario_creacion?.let{
                InfoRow(icon = R.drawable.ic_circle_user_round, text = "Creado por: ${it.nombre_1} ${it.apellido_1}")
            }

            // ── Botón de acción ──────────────────────────────────────────────
            val textButton = if (proceso.estado?.id == 7) "Iniciar Proceso" else "Continuar Proceso"

            Button(
                onClick = { onInitProcess(proceso) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource( if (proceso.estado?.id == 7) R.drawable.ic_plus else R.drawable.ic_step_forward),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = textButton,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
private fun separarNumerosTelefonicos(input: String): List<String> {
    val sinEspacios = input.replace("\\s".toRegex(), "")
    val numeros = mutableListOf<String>()
    var index = 0

    while (index < sinEspacios.length) {
        val fin = minOf(index + 10, sinEspacios.length)
        val numero = sinEspacios.substring(index, fin)

        if (numero.length == 10) {
            numeros.add(numero)
        }

        index += 10
    }
    return numeros
}

@Composable
private fun LoadMoreTrigger(
    viewModel: ProcesoViewModel,
    filters: List<Filter>,
    sorting: List<Sorting>
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        when {
            isLoading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 20.dp),
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
                                text = "Cargando más procesos...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            currentPage > totalPages -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp),
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
                                text = "No hay más procesos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            currentPage <= totalPages -> {
                LaunchedEffect(Unit) {
                    viewModel.consultMoreProcess(
                        filters = filters,
                        sorting = sorting
                    )
                }
            }
        }
    }
}