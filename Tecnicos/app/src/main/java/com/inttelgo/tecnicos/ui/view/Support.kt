package com.inttelgo.tecnicos.ui.view

import android.content.Context
import android.os.Build
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.inttelgo.tecnicos.components.LocationSection
import com.inttelgo.tecnicos.components.MediaPreview
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.ModernTopAppBar
import com.inttelgo.tecnicos.components.ObservationBox
import com.inttelgo.tecnicos.components.PhoneCard
import com.inttelgo.tecnicos.components.SectionTitle
import com.inttelgo.tecnicos.components.TypeSection
import com.inttelgo.tecnicos.components.formatDate
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
    val isLoadingEvidencias = remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val userPreferences = UserPreferences(context)
    val expanded = remember { mutableStateOf(false) }
    val filters = remember { mutableStateListOf(Filter("id_obs_ticket", "contains", "")) }
    val sorting = remember { Sorting("fecha", true) }

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
                                                HistorySection(history, isLoadingEvidencias.value) {
                                                    isLoadingEvidencias.value = true
                                                    viewModel.consultEvidencias(history.id)
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
            if (isLoadingEvidencias.value) {
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
        isLoadingEvidencias.value = false
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

    // Preview de evidencias
    evidencias?.let {
        if(it.isNotEmpty()){
            isLoadingEvidencias.value = false
            MediaPreview(it) {
                viewModel.clearEvidencias()
            }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
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

            // ── Header: ID, Prioridad y Estado ───────────────────────────
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
                        text = "Ticket #${ticket.id}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ticket.estado?.let {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = it.descripcion ?: "Sin estado",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ── Cliente ──────────────────────────────────────────────────
            ticket.cliente?.let { c ->
                SectionTitle(icon = R.drawable.ic_circle_user_round, title = "Cliente")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar iniciales
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "${c.nombre1.firstOrNull() ?: ""}${c.apellido1.firstOrNull() ?: ""}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = listOfNotNull(c.nombre1, c.nombre2, c.apellido1, c.apellido2)
                                .filter { it.isNotBlank() }.joinToString(" "),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        InfoRow(icon = R.drawable.ic_circle_user_round, text = c.identificacion)
                        InfoRow(icon = R.drawable.ic_mail, text = c.correo)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PhoneCard(c.telefono1)
                            c.telefono2?.let { PhoneCard(it) }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ── Tipo ─────────────────────────────────────────────────────

            SectionTitle(icon = R.drawable.ic_ethernet_port, title = "Tipo ticket")
            ticket.tipo?.descripcion?.let{ TypeSection(descripcion = it) }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ── Cuenta ───────────────────────────────────────────────────
            ticket.cuenta?.let { cuenta ->
                CuentaInfoSection(cuenta = cuenta)
                LocationSection(direccion = cuenta.direccion)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ── Fechas ───────────────────────────────────────────────────
            SectionTitle(icon = R.drawable.ic_calendar_days, title = "Fechas")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateChip(label = "Creación", date = ticket.fecha_hora, modifier = Modifier.weight(1f))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            // ── Observaciones ────────────────────────────────────────────
            if (ticket.observacion_ticket.isNotBlank()) {
                SectionTitle(icon = R.drawable.ic_info, title = "Observación")
                ObservationBox(text = ticket.observacion_ticket)
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(60.dp)
                ) {
                    IconButton(
                        onClick = onViewEvidence,
                        modifier = Modifier.fillMaxSize(),
                        enabled = !isLoadingEvidencias
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_eye),
                            contentDescription = "Ver evidencia",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Text(
                    text = "Evidencia",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Observación:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = observacion.descripcion.takeIf { it.isNotBlank() }
                                ?: "Sin observación registrada",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (observacion.descripcion.isNotBlank())
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = if (observacion.descripcion.isNotBlank())
                                    FontStyle.Normal else FontStyle.Italic
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = formatDate(observacion.fecha),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                observacion.usuario?.let { usuario ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_circle_user_round),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .size(14.dp)
                                .wrapContentSize(Alignment.Center)
                        )

                        Text(
                            text = buildString {
                                append(usuario.nombre_1 +" "+usuario.apellido_1)
                            }.takeIf { it.isNotBlank() } ?: "Usuario",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}