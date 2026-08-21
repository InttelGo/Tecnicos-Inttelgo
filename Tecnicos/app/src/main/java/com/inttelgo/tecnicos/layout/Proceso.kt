package com.inttelgo.tecnicos.layout

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.EmptyStateCard
import com.inttelgo.tecnicos.components.FechaInfo
import com.inttelgo.tecnicos.components.InfoDateChip
import com.inttelgo.tecnicos.components.InfoRow
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.ObservationBox
import com.inttelgo.tecnicos.components.PhoneCard
import com.inttelgo.tecnicos.components.SectionTitle
import com.inttelgo.tecnicos.components.estadoLabel
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.Proceso
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.Model.assignedToUserFilter
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.viewmodel.ProcesoViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private enum class InstallationDayFilter(val label: String, val dayOffset: Long) {
    YESTERDAY("Ayer", -1),
    TODAY("Hoy", 0),
    TOMORROW("Mañana", 1),
    TWO_DAYS_AGO("Hace 2 días", -2)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun installationDateForOffset(dayOffset: Long): String {
    return LocalDate.now(ZoneId.of("America/Bogota"))
        .plusDays(dayOffset)
        .format(DateTimeFormatter.ISO_LOCAL_DATE) // yyyy-MM-dd
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Proceso(
    navigateToInstalacion: (id: String) -> Unit,
    context: Context
) {
    val viewModel: ProcesoViewModel = remember { ProcesoViewModel() }
    val userPreferences = remember { UserPreferences(context) }
    val procesos by viewModel.procesosData.collectAsState()
    val checkProcessData by viewModel.checkProcessData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingInstalacion by viewModel.isLoadingInstalacion.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val selectedDayFilter = remember { mutableStateOf(InstallationDayFilter.TODAY) }
    val filters = remember {
        mutableStateListOf(
            Filter(column = "id_estado", operator = "in", value = listOf("7", "8")),
            Filter(column = "id_instalacion", operator = "contains", value = "", logic = "AND"),
            Filter(
                column = "installation_at",
                operator = "equals",
                value = installationDateForOffset(InstallationDayFilter.TODAY.dayOffset),
                logic = "AND"
            )
        )
    }
    val sorting = remember { listOf(Sorting("id_instalacion", true)) }
    val search = remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)

    LaunchedEffect(search.value, selectedDayFilter.value) {
        if (hasInternetConnection.value) {
            val userId = userPreferences.getUser()?.id
            if (userId == null) return@LaunchedEffect

            val installationDate = installationDateForOffset(selectedDayFilter.value.dayOffset)
            val updatedFilters = mutableListOf<Filter>()

            // 1) Estados abiertos (siempre aplica, también para asistente)
            updatedFilters.add(
                Filter(
                    column = "id_estado",
                    operator = "in",
                    value = listOf("7", "8")
                )
            )

            // 2) Asignado como operador O asistente (grupo OR)
            updatedFilters.add(assignedToUserFilter(userId))

            // 3) Búsqueda por id
            updatedFilters.add(
                Filter(
                    column = "id_instalacion",
                    operator = if (search.value.length > 3) "equals" else "contains",
                    value = search.value,
                    logic = "AND"
                )
            )

            // 4) Fecha de instalación
            updatedFilters.add(
                Filter(
                    column = "installation_at",
                    operator = "equals",
                    value = installationDate,
                    logic = "AND"
                )
            )
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
            // Header: búsqueda + filtro por fecha de instalación
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
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

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(InstallationDayFilter.entries.toList()) { dayFilter ->
                            val selected = selectedDayFilter.value == dayFilter
                            Surface(
                                modifier = Modifier.clickable {
                                    selectedDayFilter.value = dayFilter
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = dayFilter.label,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        textAlign = TextAlign.Center
                                    )
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
                            onViewMore = { proceso ->
                                proceso.id?.let { id ->
                                    navigateToInstalacion(id.toString())
                                }
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
        if(it.id=="finish"){
            ModernDialog(
                type = DialogType.SUCCESS,
                message = it.message,
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
    }

    // Diálogo de error
    errorMessage?.let {
        ModernDialog(
            type = DialogType.ERROR,
            message = it.message,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ProcessList(
    procesos: List<Proceso>,
    onViewMore: (Proceso) -> Unit,
    onLoadMore: @Composable () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(procesos) { proceso ->
            CardProceso(
                proceso = proceso,
                onViewMore = onViewMore
            )
        }

        item {
            onLoadMore()
        }
    }
}

@SuppressLint("UseKtx")
@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CardProceso(
    proceso: Proceso,
    onViewMore: (Proceso) -> Unit
) {
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
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Header: ID + Estado ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorResource(id = R.color.info).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "ID: ${proceso.id?.toString() ?: "N/A"}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colorResource(id = R.color.info),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                val statusColor = Color(android.graphics.Color.parseColor(proceso.estado?.color))
                Surface(shape = RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                    Text(
                        text = estadoLabel(proceso.estado),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // ── Identidad: Nombre ───────────────────────
            Text(
                text = proceso.nombre?.takeIf { it.isNotBlank() } ?: "Nombre no disponible",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // ── Contacto: Teléfonos + Correo ─────────────────────────────
            if (!proceso.telefonos.isNullOrBlank()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_phone, title = "Contacto")
                val telefonos = try {
                    separarNumerosTelefonicos(proceso.telefonos)
                } catch (e: Exception) {
                    Log.e("ProcesoView", "Error al separar los números de teléfono", e)
                    emptyList()
                }
                if (telefonos.isNotEmpty()) {
                    LazyRow ( horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(telefonos) { numero -> PhoneCard(numero) }
                    }
                }
            }

            // ── Ubicación ─────────────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SectionTitle(icon = R.drawable.ic_map_pin, title = "Ubicación")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column (
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    proceso.direccion?.let { direccion ->
                        val texto = if (!proceso.condominio.isNullOrBlank()) {
                            "$direccion, ${proceso.condominio}"
                        } else {
                            direccion
                        }
                        InfoRow(icon = null, text = texto)
                    }
                    proceso.barrio?.descripcion?.let {
                        InfoRow(icon = null, text = "Barrio: $it")
                    }
                }
            }

            // ── Plan ──────────────────────────────────────────────────────
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SectionTitle(icon = R.drawable.ic_notebook_text, title = "Plan")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row (
                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    proceso.plan?.id?.let {
                        Text(
                            text = "$it MB",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    proceso.tipo_plan?.descripcion?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    proceso.tipo_servicio?.descripcion?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Solo Creado, Actualizado e Inicio tienen un usuario asociado
            // (create_by, update_by). La asignación usa operator_by / assistant_by.
            val fechasInfo = listOfNotNull(
                proceso.installation_at?.takeIf { it.isNotBlank() }?.let { FechaInfo("Agendado", it, null) },
            )
            if (fechasInfo.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_info, title = "Información Adicional")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    fechasInfo.forEach { info ->
                        InfoDateChip(info = info, modifier = Modifier.fillMaxWidth())
                    }
                }
            }


            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SectionTitle(icon = R.drawable.ic_eye, title = "Observacion")
            // ── Observación ───────────────────────────────────────────────
            proceso.observacion?.descripcion?.let { ObservationBox(it) }

            // ── Acciones ──────────────────────────────────────────────────
            Button(
                onClick = { onViewMore(proceso) },
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
                Text(
                    text = "Ver más",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

fun separarNumerosTelefonicos(input: String): List<String> {
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