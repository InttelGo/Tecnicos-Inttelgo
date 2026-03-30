package com.inttelgo.tecnicos.ui.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.PasswordField
import com.inttelgo.tecnicos.components.TextField
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.viewmodel.LoginViewModel


@Composable
fun LoginScreen(context: Context, navigateToHome: () -> Unit) {
    val viewModelL: LoginViewModel = remember { LoginViewModel() }
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage by viewModelL.errorMessage.collectAsState()
    val isLoading by viewModelL.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(40.dp))

            // Login Form Card
            LoginFormCard(
                userName = userName,
                password = password,
                isLoading = isLoading == true,
                onLogin = {
                    viewModelL.loginWithEmail(context, userName.value, password.value, navigateToHome)
                }
            )

            Spacer(Modifier.height(40.dp))
        }
    }

    errorMessage?.let {
        ModernDialog(
            type = DialogType.ERROR,
            message = it,
            title = "¡Error!",
            onCancel = {
                viewModelL.clearMessages()
            },
            onSuccess = {
                viewModelL.clearMessages()
            },
            cancelText = "Cerrar",
            successText = "Continuar"
        )
    }
}


@Composable
private fun LoginFormCard(
    userName: MutableState<String>,
    password: MutableState<String>,
    isLoading: Boolean,
    onLogin: () -> Unit
) {
    val isFormValid = userName.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Form Header
            FormHeader()

            // Form Fields
            LoginFormFields(userName, password)

            // Login Button
            LoginButton(
                isLoading = isLoading,
                disabled = !isFormValid,
                onLogin = onLogin
            )

            // Footer
            FormFooter()
        }
    }
}
@Composable
private fun FormHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Logo Container
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.onSecondary,
            shadowElevation = 12.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_mano),
                    contentDescription = "Inttelgo Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )

        Text(
            text = "Ingresa tus credenciales para continuar",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun LoginFormFields(
    userName: MutableState<String>,
    password: MutableState<String>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Username Field
        TextField(
            value = userName.value,
            onValueChange = { userName.value = it },
            label = "Nombre de Usuario",
            leadingIcon = R.drawable.ic_circle_user_round,
            placeholder = "Ingresa tu usuario",
            true
        )

        // Password Field
        PasswordField(
            value = password.value,
            onValueChange = { password.value = it },
            label = "Contraseña",
            placeholder = "Ingresa tu contraseña",
            true
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    disabled: Boolean,
    onLogin: () -> Unit
) {
    Button(
        onClick = onLogin,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading && !disabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Iniciando sesión...",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        } else {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun FormFooter() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp
        )

        Text(
            text = "Inttelgo • Técnicos Móvil",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "Versión 2.0",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        )
    }
}