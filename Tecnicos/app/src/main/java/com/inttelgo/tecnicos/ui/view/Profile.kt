package com.inttelgo.tecnicos.ui.view

import com.inttelgo.tecnicos.BuildConfig
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.EmptyStateCard
import com.inttelgo.tecnicos.components.InfoRow
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.SectionTitle
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.logic.Model.UsuarioAuth
import com.inttelgo.tecnicos.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    context: Context,
    onLogout: () -> Unit
) {
    val viewModel: ProfileViewModel = remember { ProfileViewModel(context) }
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val showLogoutDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }

    LaunchedEffect (Unit) {
        if (hasInternetConnection.value) {
            viewModel.loadUserProfile(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi Perfil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(onClick = { showEditDialog.value = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_pencil),
                            contentDescription = "Editar perfil",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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

                // Cargando perfil
                isLoading && userProfile == null -> {
                    AnimatedIcon(Modifier.fillMaxSize())
                }

                // Error al cargar
                !isLoading && userProfile == null -> {
                    EmptyStateCard(
                        icon = R.drawable.ic_octagon_alert,
                        title = "Error al cargar",
                        message = "No se pudo cargar la información del perfil",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Mostrar perfil
                else -> {
                    ProfileContent(
                        userProfile = userProfile!!,
                        onLogoutClick = { showLogoutDialog.value = true }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog.value) {
        ModernDialog(
            type = DialogType.WARNING,
            message = "¿Estás seguro de que deseas cerrar sesión?",
            title = "Cerrar Sesión",
            onCancel = {
                showLogoutDialog.value = false
            },
            onSuccess = {
                showLogoutDialog.value = false
                viewModel.logout()
                onLogout()
            },
            cancelText = "Cancelar",
            successText = "Cerrar"
        )
    }

    // Diálogo de edición de perfil
    if (showEditDialog.value) {
        EditProfileDialog(
            userProfile = userProfile,
            onDismiss = { showEditDialog.value = false },
            onConfirm = { name, email, phone ->
                viewModel.updateProfile(name, email, phone, context)
                showEditDialog.value = false
            }
        )
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
            successText = "Entendido"
        )
    }
}

@Composable
private fun ProfileContent(
    userProfile: UsuarioAuth,
    onLogoutClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header con avatar y nombre
        item {
            ProfileHeader(userProfile)
        }

        // Información personal
        item {

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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Informacion Personal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    SectionTitle(title = "Nombre")
                    InfoRow(
                        icon = R.drawable.ic_circle_user_round, text = listOfNotNull(
                            userProfile.name1,
                            userProfile.name2,
                            userProfile.lastname1,
                            userProfile.lastname2
                        ).joinToString(" ").ifBlank { " " }
                    )
                    SectionTitle(title = "Correo Personal")
                    InfoRow(
                        icon = R.drawable.ic_mail,
                        text = userProfile.emailPersonal ?: "No disponible"
                    )
                    SectionTitle(title = "Telefono")
                    InfoRow(
                        icon = R.drawable.ic_phone,
                        text = userProfile.phone1 ?: "No disponible"
                    )
                    SectionTitle(title = "Rol")
                    InfoRow(icon = R.drawable.ic_info, text = userProfile.profile?.rol ?: "N/A")
                }
            }
        }

        // Información de la app
        item {
            AppInfoSection()
        }

        // Configuración de cuenta
        item {
            AccountSettingsSection(
                onLogoutClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun ProfileHeader(userProfile: UsuarioAuth?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(100.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = userProfile?.name1?.firstOrNull()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // Nombre
            Text(
                text = userProfile?.name1 ?: "Usuario",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                textAlign = TextAlign.Center
            )

            // Email
            Text(
                text = userProfile?.emailPersonal ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )

            // Badge de rol
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            ) {
                Text(
                    text = userProfile?.profile?.rol ?: "Usuario",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun AccountSettingsSection(
    onLogoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        SettingsItem(
            icon = R.drawable.ic_log_out,
            title = "Cerrar sesión",
            onClick = onLogoutClick,
            isDestructive = true
        )
    }
}

@Composable
private fun SettingsItem(
    icon: Int,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isDestructive)
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AppInfoSection() {
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Versión de la App",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = BuildConfig.VERSION_NAME,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    userProfile: UsuarioAuth?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String) -> Unit
) {
    val name = remember { mutableStateOf(userProfile?.name1 ?: "") }
    val email = remember { mutableStateOf(userProfile?.emailPersonal ?: "") }
    val phone = remember { mutableStateOf(userProfile?.phone1 ?: "") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Editar Perfil",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone.value,
                    onValueChange = { phone.value = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            onConfirm(name.value, email.value, phone.value)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}