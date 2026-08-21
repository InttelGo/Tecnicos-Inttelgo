package com.inttelgo.tecnicos.ui.view

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.CuentaInfoSection
import com.inttelgo.tecnicos.components.DateChip
import com.inttelgo.tecnicos.components.EmptyHistoryCard
import com.inttelgo.tecnicos.components.EmptyStateCard
import com.inttelgo.tecnicos.components.HistoryToggle
import com.inttelgo.tecnicos.components.InfoRow
import com.inttelgo.tecnicos.components.MediaPreview
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.ModernTopAppBar
import com.inttelgo.tecnicos.components.ObservationHistoryCard
import com.inttelgo.tecnicos.components.PhoneCard
import com.inttelgo.tecnicos.components.SectionTitle
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.ObsTicket
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.Model.Ticket
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.viewmodel.SoporteViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(idSuport: String, context: Context, navigateToUploadImage: (id: String, type: String) -> Unit, navigateToProfile: () -> Unit) {
    val viewModel: SoporteViewModel = remember { SoporteViewModel() }
    val showHistory = remember { mutableStateOf(false) }
    val ticket by viewModel.ticketData.collectAsState()
    val consultCheck by viewModel.consultCheck.collectAsState()
    val histories by viewModel.histories.collectAsState()
    val evidencias by viewModel.evidencias.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val isLoadingEvidencias by viewModel.loadingEvidencias.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val userPreferences = UserPreferences(context)
    val expanded = remember { mutableStateOf(false) }
    val filters = remember { mutableStateListOf(Filter("id", "contains", "")) }
    // Ordenar historial por fecha de creación (más recientes primero)
    val sorting = remember { Sorting("create_at", true) }

    if (hasInternetConnection.value) {
        LaunchedEffect(Unit) {
            viewModel.consultTicketById(idSuport)
        }
    }

    LaunchedEffect(showHistory.value) {
        if(showHistory.value){
            viewModel.consultMoreObsByTicket(idSuport, filters, sorting = sorting)
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
            FloatingButtons(idSuport, navigateToUploadImage, consultCheck && !isLoading)
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
                horizontalAlignment = Alignment.CenterHorizontally,
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

                    // Cargando ticket inicial
                    !consultCheck && isLoading -> {
                        AnimatedIcon(Modifier.fillMaxSize())
                    }

                    // Error al cargar
                    !consultCheck && !isLoading -> {
                        EmptyStateCard(
                            icon = R.drawable.ic_octagon_alert,
                            title = "No se pudo cargar",
                            message = "No se encontró la información del ticket",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Contenido cargado
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                TicketInfoCard(ticket)
                            }

                            item {
                                HistoryToggle(showHistory)
                            }

                            if (showHistory.value) {
                                when {
                                    // Cargando historial inicial
                                    isLoading && currentPage == 1 && histories.isNullOrEmpty() -> {
                                        item {
                                            LoadingHistoryCard()
                                        }
                                    }

                                    // Sin historial
                                    !isLoading && histories.isNullOrEmpty() -> {
                                        item {
                                            EmptyHistoryCard()
                                        }
                                    }

                                    // Mostrar historial
                                    else -> {
                                        histories?.let { historyList ->
                                            items(historyList) { history ->
                                                HistorySection(history, isLoadingEvidencias) {
                                                    viewModel.consultEvidencias(idSuport, history.id)
                                                }
                                            }

                                            // Load more trigger
                                            item {
                                                HistoryLoadMoreTrigger(
                                                    viewModel = viewModel,
                                                    idTicket = idSuport,
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

            // Overlay de carga de evidencias
            if (isLoadingEvidencias) {
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
                                text = "Cargando evidencias...",
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
            successText = "Entendido"
        )
    }

    // Preview de evidencias (incluye lista vacía → "Sin evidencias")
    evidencias?.let {
        MediaPreview(it) {
            viewModel.clearEvidencias()
        }
    }
}

@Composable
private fun LoadingHistoryCard() {
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
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )
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
    viewModel: SoporteViewModel,
    idTicket: String,
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
                    viewModel.consultMoreObsByTicket(
                        id = idTicket,
                        filters = filters,
                        sorting = sorting
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingButtons(
    idSuport: String,
    navigateToUploadImage: (String, String) -> Unit,
    enabled: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = { navigateToUploadImage(idSuport, "ticket") },
            containerColor = if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (enabled) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }

        FloatingActionButton(
            onClick = { navigateToUploadImage(idSuport, "finalizar ticket") },
            containerColor = if (enabled) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (enabled) MaterialTheme.colorScheme.onSecondary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            shape = CircleShape,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_upload),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TicketInfoCard(ticket: Ticket?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        if (ticket == null) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay ticket seleccionado",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            return@Card
        }

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
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "#${ticket.id}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                ticket.status?.let {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = it.descripcion.orEmpty().ifBlank { "Sin estado" },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Cliente
            ticket.cliente?.let { c ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_circle_user_round, title = "Cliente")
                Text(
                    text = listOfNotNull(c.nombre1, c.nombre2, c.apellido1, c.apellido2)
                        .filter { it.isNotBlank() }.joinToString(" ")
                        .ifBlank { "Sin nombre" },
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                c.correo.takeIf { it.isNotBlank() }?.let {
                    InfoRow(icon = R.drawable.ic_mail, text = it)
                }
                val telefonos = listOfNotNull(
                    c.telefono1.takeIf { it.isNotBlank() },
                    c.telefono2?.takeIf { it.isNotBlank() }
                )
                if (telefonos.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(telefonos) { numero -> PhoneCard(numero) }
                    }
                }
            }

            // Tipo
            ticket.type?.descripcion?.takeIf { it.isNotBlank() }?.let { tipo ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_ethernet_port, title = "Tipo")
                InfoRow(icon = null, text = tipo)
            }

            // Servicio
            ticket.service?.let { service ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_notebook_text, title = "Servicio")
                CuentaInfoSection(cuenta = service)
                val planText = buildString {
                    service.plan?.let { append("$it MB ") }
                    service.tipo_plan?.descripcion?.let { append(it) }
                    service.tipo_servicio?.descripcion?.let {
                        if (isNotEmpty()) append(" · ")
                        append(it)
                    }
                }
                if (planText.isNotBlank()) {
                    InfoRow(icon = null, text = planText)
                }
            }

            // Fechas
            val fechas = listOfNotNull(
                ticket.reserved_at.takeIf { it.isNotBlank() }?.let { "Fecha realización" to it },
            )
            if (fechas.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SectionTitle(icon = R.drawable.ic_calendar_days, title = "Fechas")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(fechas) { (label, fecha) ->
                        DateChip(label = label, date = fecha, modifier = Modifier)
                    }
                }
            }

            // Operador / Asistente
            val asignados = listOfNotNull(
                ticket.operator_by?.let { "Operador" to it },
                ticket.assistant_by?.let { "Asistente" to it },
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
    observacion: ObsTicket,
    isLoadingEvidencias: Boolean,
    onViewEvidence: () -> Unit
) {
    val nombreUsuario = observacion.create_by?.let { usuario ->
        listOfNotNull(usuario.nombre_1, usuario.apellido_1)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { null }
    }

    ObservationHistoryCard(
        descripcion = observacion.content,
        fecha = observacion.create_at,
        usuarioNombre = nombreUsuario,
        isLoadingEvidencias = isLoadingEvidencias,
        onViewEvidence = onViewEvidence
    )
}