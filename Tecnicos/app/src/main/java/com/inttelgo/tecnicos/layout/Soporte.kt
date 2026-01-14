package com.inttelgo.tecnicos.layout

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.inttelgo.tecnicos.logic.Model.Ticket
import com.inttelgo.tecnicos.logic.process.homeProcess
import com.inttelgo.tecnicos.viewmodel.HomeViewModel
import com.inttelgo.tecnicos.viewmodel.SoporteViewModel

@SuppressLint("DiscouragedApi")
@Composable
fun Soporte(
    viewModelH: HomeViewModel,
    navigateToSupport: (idSupport: String) -> Unit,
    context: Context
) {
    val viewModel: SoporteViewModel = remember { SoporteViewModel() }
    val filters = remember { mutableStateListOf(Filter("id_ticket", "contains", ""), Filter("id_estado_ticket", "equals", "1", logic = "AND")) }
    val sorting = remember { Sorting("fecha_hora", true) }
    val prioritySelected = remember { mutableIntStateOf(0) }
    val tickets by viewModel.ticketsData.collectAsState()
    val barrios by viewModelH.barrios.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)

    // Content Section
    if(hasInternetConnection.value){
        LaunchedEffect(prioritySelected.intValue) {
            viewModel.consultMoreTickets(filters= filters, sorting= sorting )
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
                isLoading && currentPage == 1 && tickets.isNullOrEmpty() -> {
                    AnimatedIcon(Modifier.fillMaxSize(), "Cargando tickets...")
                }

                // Sin tickets después de cargar
                !isLoading && tickets.isNullOrEmpty() -> {
                    EmptyStateCard(
                        icon = Icons.Default.Close,
                        title = "No hay tickets",
                        message = "No se encontraron tickets asignados",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Cargando barrios
                barrios.isNullOrEmpty() -> {
                    AnimatedIcon(Modifier.fillMaxSize(), "Cargando información...")
                }

                // Mostrar contenido
                else -> {
                    val ticketsArreglados = tickets?.let {
                        barrios?.let { it1 -> homeProcess().generarConjunto(it, it1) }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        ticketsArreglados?.forEach { (barrio, ticketsConBarrio) ->
                            if (ticketsConBarrio.isNotEmpty()) {
                                item {
                                    BarrioHeader(barrio = barrio, ticketCount = ticketsConBarrio.size)
                                }
                                items(
                                    items = ticketsConBarrio,
                                    key = { it.id_ticket }
                                ) { ticket ->
                                    TicketCard(
                                        ticket = ticket,
                                        context = context,
                                        onViewMore = { navigateToSupport(ticket.id_ticket) }
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
private fun BarrioHeader(barrio: String, ticketCount: Int) {
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
                text = "$ticketCount ticket${if (ticketCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun TicketCard(
    ticket: Ticket,
    context: Context,
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
                        text = "Ticket #${ticket.id_ticket}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                ticket.prioridad?.let { PriorityCard(it) }
            }

            Spacer(Modifier.height(20.dp))

            // Main Content
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ticket Type Icon
                TicketTypeSection(ticket = ticket, context = context)

                // Client Information
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ticket.cuenta?.let {
                        CuentaInfoSection(cuenta = it)
                        LocationSection(direccion = it.direccion)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            ObservationSection(ticket.observacion_ticket)
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

@SuppressLint("DiscouragedApi")
@Composable
private fun TicketTypeSection(ticket: Ticket, context: Context) {
    val imageResId = remember(ticket.tipo?.nombre_icono) {
        context.resources.getIdentifier(ticket.tipo?.nombre_icono, "drawable", context.packageName)
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(100.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(imageResId),
                    contentDescription = "Tipo de ticket",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.height(8.dp))
                ticket.tipo?.let {
                    Text(
                        text = it.descripcion,
                        style = MaterialTheme.typography.labelSmall.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 12.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "No disponible",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun LoadMoreTrigger(
    viewModel: SoporteViewModel,
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
                                text = "Cargando más tickets...",
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
                                text = "No hay más tickets",
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
                    viewModel.consultMoreTickets(
                        filters = filters,
                        sorting = sorting
                    )
                }
            }
        }
    }
}