package com.inttelgo.tecnicos.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.inttelgo.tecnicos.R
import androidx.core.net.toUri

@Composable
fun ButtonRainbow(
    text: String,
    modifier: Modifier,
    flag: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = if (enabled) 4.dp else 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (flag) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                    } else {
                        Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    }
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (flag) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            )
        }
    }
}
@Composable
fun CustomButton(
    isLoading: Boolean,
    disabled: Boolean,
    title: String,
    chargeTitle: String,
    disabledTitle: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = !isLoading && !disabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                disabled && !isLoading -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.primary
            },
            contentColor = when {
                disabled && !isLoading -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onPrimary
            },
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (disabled && !isLoading) 0.dp else 8.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        when {
            isLoading -> {
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
                        text = chargeTitle,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            disabled -> {
                Text(
                    text = disabledTitle,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            else -> {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: Int?,
    placeholder: String,
    required: Boolean = false
) {
    // Validar si el campo está vacío y es requerido
    val isEmpty = value.trim().isEmpty()
    val showError = required && isEmpty

    Text(
        text = if (showError) "$label *" else label,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = if (showError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                    tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        },
        isError = showError,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = if (showError) MaterialTheme.colorScheme.error.copy(alpha = 0.05f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            unfocusedContainerColor = if (showError) MaterialTheme.colorScheme.error.copy(alpha = 0.02f) else MaterialTheme.colorScheme.surfaceVariant,
            errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
            cursorColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    )

    // Mensaje de error
    if (showError) {
        Text(
            text = "Este campo es obligatorio",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    required: Boolean = false
) {
    val isEmpty = value.trim().isEmpty()
    val showError = required && isEmpty

    val passwordVisible = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = if (showError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_key_round),
                    contentDescription = null,
                    tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                val image = if (passwordVisible.value)
                    R.drawable.ic_eye_closed
                else R.drawable.ic_eye

                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(
                        painter = painterResource(image),
                        tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentDescription = if (passwordVisible.value) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible.value)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = if (showError) MaterialTheme.colorScheme.error.copy(alpha = 0.05f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                unfocusedContainerColor = if (showError) MaterialTheme.colorScheme.error.copy(alpha = 0.02f) else MaterialTheme.colorScheme.surfaceVariant,
                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                cursorColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        )

        if (showError) {
            Text(
                text = "Este campo es obligatorio",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    required: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
    minLines: Int = 3,
    maxLines: Int = 6,
    leadingIcon: ImageVector? = null
) {
    val isEmpty = value.trim().isEmpty()
    val showError = (required && isEmpty) || isError

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label
        label?.let {
            Text(
                text = if (required && isEmpty) "$it *" else it,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // TextArea
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = if (showError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = when {
                            showError -> MaterialTheme.colorScheme.error
                            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                }
            },
            enabled = enabled,
            isError = showError,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = if (showError)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                unfocusedContainerColor = if (showError)
                    MaterialTheme.colorScheme.error.copy(alpha = 0.02f)
                else if (enabled)
                    MaterialTheme.colorScheme.surfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                cursorColor = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )

        // Mensaje de error
        if (showError && required && isEmpty) {
            Text(
                text = "Este campo es obligatorio",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhoneCard(number: String) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val color = colorResource(R.color.gradient_end)
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL, "tel:$number".toUri())
                context.startActivity(intent)
            },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                copyToClipboard(context, number)
                Toast.makeText(context, "Número copiado", Toast.LENGTH_SHORT).show()
            }
        ),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer,
        border = BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 6.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_copy),
                    contentDescription = "Llamar a $number",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(12.dp),
                    tint = color
                )
            }
            Text(
                text = number,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}


// Function to copy to clipboard (you might have this defined elsewhere)
fun copyToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Numero Telefonico", text)
    clipboardManager.setPrimaryClip(clipData)
}
@Composable
fun rememberNetworkConnectivityState(context: Context): State<Boolean> {
    val connectivityState = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectivityState.value = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                connectivityState.value = false
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        // Check initial connectivity state
        val isConnected = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
        connectivityState.value = isConnected
    }
    return connectivityState
}

@Composable
fun NumberField(
    number: Int,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showButtons: Boolean = true,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    required: Boolean = false,
    onChange: (Int) -> Unit
) {
    val textValue = remember { mutableStateOf(number.toString()) }
    val currentValue = remember { mutableIntStateOf(number) }

    // Validar si el campo está vacío y es requerido
    val isEmpty = currentValue.intValue == 0 && required
    val showError = required && isEmpty

    // Sincronizamos el valor cuando cambia desde fuera
    LaunchedEffect(number) {
        currentValue.intValue = number
        textValue.value = number.toString()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textValue.value,
                onValueChange = { newValue ->
                    textValue.value = newValue
                    when {
                        newValue.isEmpty() -> {
                            currentValue.intValue = minValue
                            onChange(minValue)
                        }
                        newValue.toIntOrNull() != null -> {
                            val parsedValue = newValue.toInt().coerceIn(minValue, maxValue)
                            currentValue.intValue = parsedValue
                            textValue.value = parsedValue.toString()
                            onChange(parsedValue)
                        }
                    }
                },
                label = { Text(label) },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                isError = showError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = if (showButtons) RoundedCornerShape(16.dp) else MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (showError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (showError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outlineVariant,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    focusedContainerColor = if (showError)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    unfocusedContainerColor = if (showError)
                        MaterialTheme.colorScheme.error.copy(alpha = 0.02f)
                    else if (enabled)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                    errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                    cursorColor = if (showError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                placeholder = {
                    Text(
                        text = "0",
                        color = if (showError)
                            MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            )

            if (showButtons) {
                IconButton(
                    onClick = {
                        val newValue = (currentValue.intValue - 1).coerceAtLeast(minValue)
                        currentValue.intValue = newValue
                        textValue.value = newValue.toString()
                        onChange(newValue)
                    },
                    enabled = enabled && currentValue.intValue > minValue,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Decrementar",
                        tint = if (showError)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showButtons) {
                IconButton(
                    onClick = {
                        val newValue = (currentValue.intValue + 1).coerceAtMost(maxValue)
                        currentValue.intValue = newValue
                        textValue.value = newValue.toString()
                        onChange(newValue)
                    },
                    enabled = enabled && currentValue.intValue < maxValue,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Incrementar",
                        tint = if (showError)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Mensaje de error
        if (showError) {
            Text(
                text = "Este campo es obligatorio",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

/**
 * Overlay a pantalla completa que bloquea interacción y el botón atrás
 * mientras se envía una petición (finalizar instalación / crear observación).
 */
@Composable
fun BlockingLoadingOverlay(
    visible: Boolean,
    title: String,
    subtitle: String = "No cierres la app ni salgas de esta pantalla"
) {
    if (!visible) return

    BackHandler(enabled = true) { /* bloquea navegación atrás */ }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}