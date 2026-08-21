package com.inttelgo.tecnicos.ui.view

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.EmptyHistoryCard
import com.inttelgo.tecnicos.components.EmptyStateCard
import com.inttelgo.tecnicos.components.FechaInfo
import com.inttelgo.tecnicos.components.HistoryToggle
import com.inttelgo.tecnicos.components.InfoDateChip
import com.inttelgo.tecnicos.components.InfoRow
import com.inttelgo.tecnicos.components.MediaPreview
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.ModernTopAppBar
import com.inttelgo.tecnicos.components.ObservationHistoryCard
import com.inttelgo.tecnicos.components.PhoneCard
import com.inttelgo.tecnicos.components.SectionTitle
import com.inttelgo.tecnicos.components.estadoLabel
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.layout.separarNumerosTelefonicos
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.Observacion
import com.inttelgo.tecnicos.logic.Model.Proceso
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.Model.updateInstallationBody
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.viewmodel.ProcesoViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalacionScreen(
    idInstalacion: String,
    context: Context,
    navigateToUploadImage: (id: String, type: String) -> Unit,
    navigateToProfile: () -> Unit
) {
    val viewModel: ProcesoViewModel = remember { ProcesoViewModel() }
    val showHistory = remember { mutableStateOf(false) }
    val instalacion by viewModel.instalacionData.collectAsState()
    val consultCheck by viewModel.consultCheck.collectAsState()
    val histories by viewModel.histories.collectAsState()
    val evidencias by viewModel.evidencias.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingInstalacion by viewModel.isLoadingInstalacion.collectAsState()
    val isLoadingEvidencias by viewModel.loadingEvidencias.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val obsCurrentPage by viewModel.obsCurrentPage.collectAsState()
    val obsTotalPages by viewModel.obsTotalPages.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val userPreferences = UserPreferences(context)
    val expanded = remember { mutableStateOf(false) }
    val filters = remember { mutableStateListOf(Filter("id_obs_instalacion", "contains", "")) }
    // Ordenar historial por fecha de creación (más recientes primero)
    val sorting = remember { Sorting("fecha", true) }

    if (hasInternetConnection.value) {
        LaunchedEffect(Unit) {
            viewModel.consultInstalacion(idInstalacion)
        }
    }

    LaunchedEffect(showHistory.value) {
        if (showHistory.value) {
            viewModel.consultMoreObsByInstalacion(idInstalacion, filters, sorting = sorting)
        } else {
            viewModel.clearHistories()
        }
    }

    Scaffold(
        topBar = {
            ModernTopAppBar(
                userPreferences = userPreferences,
                expanded = expanded,
                hasInternetConnection,
                navigateToProfile
            )
        },
        floatingActionButton = {
            FloatingButtons(
                instalacion = instalacion,
                navigateToUploadImage = navigateToUploadImage,
                onInitOrContinue = { proceso ->
                    val zoneId = ZoneId.of("America/Bogota")
                    val bogotaTime = ZonedDateTime.now(zoneId)
                    val initAt = bogotaTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    viewModel.update(
                        proceso,
                        updateInstallationBody(initAt, 8),
                        navigateToUploadImage
                    )
                },
                enabled = consultCheck && !isLoading && !isLoadingInstalacion
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    !hasInternetConnection.value -> {
                        EmptyStateCard(
                            icon = R.drawable.ic_octagon_alert,
                            title = "Sin conexión",
                            message = "Por favor verifica tu conexión a internet",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    !consultCheck && isLoading -> {
                        AnimatedIcon(Modifier.fillMaxSize())
                    }

                    !consultCheck && !isLoading -> {
                        EmptyStateCard(
                            icon = R.drawable.ic_circle_off,
                            title = "No se pudo cargar",
                            message = "No se encontró la información de la instalación",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                InstalacionInfoCard(instalacion)
                            }
                            item {
                                HistoryToggle(showHistory)
                            }

                            if (showHistory.value) {
                                when {
                                    isLoading && obsCurrentPage == 1 && histories.isNullOrEmpty() -> {
                                        item { LoadingHistoryCard() }
                                    }

                                    !isLoading && histories.isNullOrEmpty() -> {
                                        item { EmptyHistoryCard() }
                                    }

                                    else -> {
                                        histories?.let { historyList ->
                                            items(historyList, key = { it.id }) { history ->
                                                HistorySection(history, isLoadingEvidencias) {
                                                    viewModel.consultEvidenciasObs(
                                                        idInstalacion,
                                                        history.id
                                                    )
                                                }
                                            }
                                            item {
                                                HistoryLoadMoreTrigger(
                                                    viewModel = viewModel,
                                                    idInstalacion = idInstalacion,
                                                    filters = filters,
                                                    sorting = sorting
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isLoadingEvidencias || isLoadingInstalacion) {
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
                                text = if (isLoadingInstalacion) "Iniciando proceso..." else "Cargando evidencias...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    errorMessage?.let {
        ModernDialog(
            type = DialogType.ERROR,
            message = it.message,
            title = "Error",
            onCancel = { viewModel.clearMessages() },
            onSuccess = { viewModel.clearMessages() },
            cancelText = "Cerrar",
            successText = "Entendido"
        )
    }

    // null = no consultado; lista vacía = observación sin archivos (estado vacío en el visor)
    evidencias?.let {
        MediaPreview(it) {
            viewModel.clearEvidencias()
        }
    }
}

@Composable
private fun FloatingButtons(
    instalacion: Proceso?,
    navigateToUploadImage: (String, String) -> Unit,
    onInitOrContinue: (Proceso) -> Unit,
    enabled: Boolean
) {
    val id = instalacion?.id?.toString() ?: return

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = { if (enabled) navigateToUploadImage(id, "ProcesoObservacion") },
            containerColor = if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (enabled) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Agregar observación",
                modifier = Modifier.size(24.dp)
            )
        }

        FloatingActionButton(
            onClick = {
                if (enabled) {
                    instalacion?.let(onInitOrContinue)
                }
            },
            containerColor = if (enabled) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (enabled) MaterialTheme.colorScheme.onSecondary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                painter = painterResource(
                    if (instalacion?.estado?.id == 7) R.drawable.ic_plus else R.drawable.ic_step_forward
                ),
                contentDescription = if (instalacion?.estado?.id == 7) "Iniciar" else "Continuar",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun InstalacionInfoCard(proceso: Proceso?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ID + Estado
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
                        text = "#${proceso?.id ?: "N/A"}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = colorResource(id = R.color.info),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                proceso?.estado?.let { estado ->
                    val statusColor = try {
                        Color(android.graphics.Color.parseColor(estado.color))
                    } catch (_: Exception) {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = statusColor.copy(alpha = 0.1f)) {
                        Text(
                            text = estadoLabel(estado),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Cliente
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SectionTitle(icon = R.drawable.ic_circle_user_round, title = "Cliente")
            Text(
                text = proceso?.nombre?.takeIf { it.isNotBlank() } ?: "Sin nombre",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            proceso?.correo?.takeIf { it.isNotBlank() }?.let {
                InfoRow(icon = R.drawable.ic_mail, text = it)
            }
            proceso?.telefonos?.takeIf { it.isNotBlank() }?.let { telefonosRaw ->
                val telefonos = try {
                    separarNumerosTelefonicos(telefonosRaw)
                } catch (e: Exception) {
                    Log.e("InstalacionScreen", "Error al separar teléfonos", e)
                    emptyList()
                }
                if (telefonos.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(telefonos) { numero -> PhoneCard(numero) }
                    }
                }
            }

            // Tipo
            proceso?.tipo_servicio?.descripcion?.takeIf { it.isNotBlank() }?.let { tipo ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_ethernet_port, title = "Tipo")
                InfoRow(icon = null, text = tipo)
            }

            // Servicio / Plan
            val planText = buildString {
                proceso?.plan?.id?.let { append("$it MB") }
                proceso?.tipo_plan?.descripcion?.let {
                    if (isNotEmpty()) append(" ")
                    append(it)
                }
            }
            if (planText.isNotBlank() || proceso?.tipo_servicio != null) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_notebook_text, title = "Servicio")
                if (planText.isNotBlank()) {
                    InfoRow(icon = null, text = planText)
                }
            }

            // Fechas
            val fechas = listOfNotNull(
                proceso?.reserved_at?.takeIf { it.isNotBlank() }?.let { "Reservación" to it },
                proceso?.init_at?.takeIf { it.isNotBlank() }?.let { "Fecha realización" to it },
                proceso?.installation_at?.takeIf { it.isNotBlank() }?.let { "Fecha instalación" to it },
            )
            if (fechas.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_calendar_days, title = "Fechas")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    fechas.forEach { (label, fecha) ->
                        InfoDateChip(
                            info = FechaInfo(label, fecha, null),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Operador / Asistente
            val asignados = listOfNotNull(
                proceso?.operator_by?.let { "Operador" to it },
                proceso?.assistant_by?.let { "Asistente" to it },
            )
            if (asignados.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_circle_user_round, title = "Asignación")
                asignados.forEach { (label, usuario) ->
                    val nombre = listOfNotNull(usuario.nombre_1, usuario.apellido_1)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .ifBlank { "Usuario" }
                    InfoRow(icon = null, text = "$label: $nombre")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistorySection(
    observacion: Observacion,
    isLoadingEvidencias: Boolean,
    onViewEvidence: () -> Unit
) {
    val nombreUsuario = observacion.usuario?.let { usuario ->
        listOfNotNull(usuario.nombre_1, usuario.apellido_1)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { null }
    }

    ObservationHistoryCard(
        descripcion = observacion.descripcion,
        fecha = observacion.fecha,
        usuarioNombre = nombreUsuario,
        isLoadingEvidencias = isLoadingEvidencias,
        onViewEvidence = onViewEvidence
    )
}

@Composable
private fun LoadingHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                Text(
                    text = "Cargando historial...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HistoryLoadMoreTrigger(
    viewModel: ProcesoViewModel,
    idInstalacion: String,
    filters: List<Filter>,
    sorting: Sorting
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val currentPage by viewModel.obsCurrentPage.collectAsState()
    val totalPages by viewModel.obsTotalPages.collectAsState()

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
                                text = "Cargando más observaciones...",
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
                                text = "No hay más observaciones",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            currentPage <= totalPages -> {
                LaunchedEffect(Unit) {
                    viewModel.consultMoreObsByInstalacion(
                        id = idInstalacion,
                        filters = filters,
                        sorting = sorting
                    )
                }
            }
        }
    }
}
