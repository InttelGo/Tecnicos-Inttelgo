package com.inttelgo.tecnicos.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.inttelgo.tecnicos.components.AnimatedIcon
import com.inttelgo.tecnicos.components.ModernTopAppBar
import com.inttelgo.tecnicos.components.NumberField
import com.inttelgo.tecnicos.components.SingleFotoInstaPreview
import com.inttelgo.tecnicos.components.SingleMediaPreview
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.layout.Installation
import com.inttelgo.tecnicos.layout.ObsTarea
import com.inttelgo.tecnicos.layout.ObsTicket
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.FotoInsta
import com.inttelgo.tecnicos.logic.persistence.UserPreferences

@RequiresApi(Build.VERSION_CODES.P)
@Preview
@Composable
fun PreviewUpladImage(){
    val context = LocalContext.current
    val navigateToHome = { /* TODO: Implement navigation to HomeScreen */ }
    val navigateToUp = { /* TODO: Implement navigation to UploadImageScreen */ }
    UploadImgScreen(id = "12345", type = "Proceso", context = context, navigateToHome = navigateToHome, navigateToUp = navigateToUp, navigateToProfile = {})
}

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("MissingPermission", "DefaultLocale", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImgScreen(id: String, type: String, context: Context, navigateToHome: () -> Unit, navigateToUp: () -> Unit, navigateToProfile: () -> Unit) {
    val hasInternetConnection = rememberNetworkConnectivityState(context)
    val uploadProgress = remember { mutableFloatStateOf(0f) }
    val userPreferences = UserPreferences(context)
    val expanded = remember { mutableStateOf(false) }
    val selectedPreviewUri = remember { mutableStateOf<Uri?>(null) }
    val selectedPreviewFotoInsta = remember { mutableStateOf<FotoInsta?>(null) }
    val isCompressing = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ModernTopAppBar(
                userPreferences = userPreferences,
                expanded = expanded,
                hasInternetConnection,
                navigateToProfile
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { innerPadding ->
        if (uploadProgress.floatValue > 0) {
                AnimatedIcon(Modifier.fillMaxSize())
        }else{

            // Render different components based on type
            when {
                type.contains("tarea", ignoreCase = true) -> {
                    ObsTarea(
                        id = id,
                        type = type,
                        context = context,
                        navigateToHome = navigateToHome,
                        navigateToUp = navigateToUp,
                        modifier = Modifier.padding(innerPadding).padding(20.dp).fillMaxSize(),
                        selectedPreviewUri,
                        isCompressing
                    )
                }
                type.contains("ticket", ignoreCase = true) -> {
                    ObsTicket(
                        id = id,
                        type = type,
                        context = context,
                        navigateToHome = navigateToHome,
                        navigateToUp = navigateToUp,
                        modifier = Modifier.padding(innerPadding).padding(20.dp).fillMaxSize(),
                        selectedPreviewUri,
                        isCompressing
                    )
                }
                type.contains("instalacion", ignoreCase = true) || type.contains("Proceso", ignoreCase = true) -> {
                    Installation(
                        id = id,
                        context = context,
                        navigateToHome = navigateToHome,
                        navigateToUp = navigateToUp,
                        modifier = Modifier.padding(innerPadding).padding(20.dp).fillMaxSize(),
                        selectedPreviewFotoInsta,
                        isCompressing
                    )
                }
            }
        }
    }
    if(selectedPreviewUri.value != null){
        SingleMediaPreview(
            uri = selectedPreviewUri,
            context = context
        ){
            selectedPreviewUri.value = null
        }
    }
    if(selectedPreviewFotoInsta.value != null){
        SingleFotoInstaPreview(
            media = selectedPreviewFotoInsta,
            context = context
        ){
            selectedPreviewFotoInsta.value = null
        }
    }
    if (isCompressing.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim),
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
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )

                    Text(
                        text = "Comprimiendo",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Text(
                        text = "Contenido multimedia",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

fun getMediaType(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)
}

@Composable
private fun ArticleItem(
    articulo: Articulo,
    onCantidadChange: (Int) -> Unit,
    enabled: Boolean
) {
    val isOptional = articulo.id.toInt() in 109..111

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = articulo.nombre,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.width(8.dp))

            NumberField(
                number = articulo.cantidad,
                label = "Cantidad" + if (isOptional) " (Opcional)" else "",
                modifier = Modifier.weight(1f),
                enabled = enabled,
                maxValue = 20,
                onChange = onCantidadChange
            )
        }
    }
}