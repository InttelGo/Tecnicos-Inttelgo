package com.inttelgo.tecnicos.components

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.inttelgo.tecnicos.R
import java.io.File
import java.time.LocalDateTime

@Composable
fun TextFlieldCustom (title: String, content: MutableState<String>, w: Dp ){
    OutlinedTextField(
        value = content.value,
        shape = RoundedCornerShape(10.dp),
        onValueChange = { content.value = it },
        label = { Text(title) },
        modifier = Modifier.width(w)
    )
}

@Composable
fun PassFlied(password: MutableState<String>, title: String,w: Dp) {
    val flag = remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),
        value = password.value,
        modifier = Modifier.width(w),
        onValueChange = { password.value = it },
        label = { Text(title) },
        visualTransformation = if (flag.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (flag.value)
                R.drawable.visibility_off_icon
           else R.drawable.visibility_icon

            IconButton(onClick = { flag.value = !flag.value }) {
                Icon(
                    painter = painterResource(image) ,
                    tint = Color.Black,
                    contentDescription = if (flag.value) "Hide password" else "Show password"
                )
            }
        }
    )
}

@Composable
fun ButtonRainbow(text: String, modifier: Modifier, flag: Boolean, onClick: () -> Unit){
    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (flag) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                            )
                        )
                    } else Modifier // No fondo para el secundario
                )
                .clip(RoundedCornerShape(10.dp))
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (flag) Color(0xFFffffff) else Color.Black
                )
            )
        }
    }
}

@Composable
fun SearchInput(search: MutableState<String>, onClick: () -> Unit){

    OutlinedTextField(
        shape = RoundedCornerShape(10.dp),
        value = search.value,
        modifier = Modifier.width(350.dp),
        onValueChange = { search.value = it },
        label = { Text("Buscar") },
        trailingIcon = {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = "Search icon",
                    tint = Color.Black, // Aplica un color base al ícono
                    modifier = Modifier.fillMaxSize() // Asegúrate de que el ícono ocupe todo el espacio del contenedor
                )
            }
        }
    )
}
@Composable
fun PhoneCard(number: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .clickable {
                // Copiar al portapapeler
                copyToClipboard(context, number)

                // llamada e instancia a fotos con el numero
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(9.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                number,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(8.dp)) // Add some spacing between text and icon
            Icon(
                painter = painterResource(R.drawable.content_copy_icon),
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(16.dp)
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
fun TextButtonForm(text: String, isPrimary: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .then(
                if (isPrimary) {
                    Modifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                        ),
                        shape = RoundedCornerShape(50.dp)
                    )
                } else Modifier // No fondo para el secundario
            )
            .border(
                width = if (isPrimary) 0.dp else 0.dp,
                color = if (isPrimary) Color.White else Color.Black,
                shape = RoundedCornerShape(50.dp)
            )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if(isPrimary)Color.White else Color.Black
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(12.dp)
        )
    }
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
            connectivityManager.getNetworkCapabilities(network)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
    onChange: (Int) -> Unit
) {
    // Convertimos el número inicial a texto, permitiendo vacío si es necesario
    val textValue = remember { mutableStateOf(number.toString()) }

    OutlinedTextField(
        value = textValue.value,
        onValueChange = { newValue ->
            textValue.value = newValue // Actualizamos el texto
            val parsedNumber = newValue.toIntOrNull() // Convertimos a Int si es posible
            if (parsedNumber != null) {
                onChange(parsedNumber) // Notificamos cambios válidos
            } else if (newValue.isEmpty()) {
                onChange(0) // Notificamos que el número es 0 si el campo está vacío
            }
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

