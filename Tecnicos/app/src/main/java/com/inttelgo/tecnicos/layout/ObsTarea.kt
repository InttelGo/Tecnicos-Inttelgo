package com.inttelgo.tecnicos.layout

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inttelgo.tecnicos.components.BlockingLoadingOverlay
import com.inttelgo.tecnicos.components.CustomButton
import com.inttelgo.tecnicos.components.LazyImages
import com.inttelgo.tecnicos.components.ModernDialog
import com.inttelgo.tecnicos.components.NumberField
import com.inttelgo.tecnicos.components.SignatureDialog
import com.inttelgo.tecnicos.components.TextArea
import com.inttelgo.tecnicos.components.rememberNetworkConnectivityState
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.viewmodel.TareaViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ObsTarea(
    id: String,
    type: String,
    context: Context,
    navigateToHome: ()-> Unit,
    navigateToUp: () -> Unit,
    modifier: Modifier,
    selectedPreviewUri:  MutableState<Uri?>,
    isCompressing: MutableState<Boolean>
){
    val viewModel: TareaViewModel = remember { TareaViewModel() }
    val isUploadingFile by viewModel.isUploadingFile.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val warningMessage by viewModel.warningMessage.collectAsState()
    val selectedImages = remember { mutableStateOf<List<Uri?>>(emptyList()) }
    val observacion =  remember { mutableStateOf("") }
    val isLoading by viewModel.uploadingLoading.collectAsState()
    // Por el momento no se solicita material al finalizar tarea
    // val isLoadingArticles by viewModel.loading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    // val articulosData by viewModel.articlesData.collectAsState()
    // val hasInternetConnection = rememberNetworkConnectivityState(context)

    val isFormValid = observacion.value.trim().isNotEmpty() && selectedImages.value.isNotEmpty()
    val showSignatureDialog = remember { mutableStateOf(false) }
    val signatureBitmap = remember { mutableStateOf<Bitmap?>(null) }

    // Por el momento no se solicita material al finalizar tarea
    /*
    if (hasInternetConnection.value) {
        LaunchedEffect(Unit) {
            viewModel.consultArticles(id)
        }
    }
    */

    if (showSignatureDialog.value && !isLoading) {
        SignatureDialog(
            onConfirm = { signature ->
                signatureBitmap.value = signature.bitmap
                showSignatureDialog.value = false
                if (type.contains("finalizar")) {
                    viewModel.finishObs(
                        id = id,
                        selectedImages = selectedImages,
                        observacion = observacion,
                        signatureBitmap = signature.bitmap,
                        context = context,
                        esEncargado = signature.esEncargado,
                        nombreEncargado = signature.nombreEncargado,
                        identificacionEncargado = signature.identificacionEncargado
                    )
                } else {
                    viewModel.createObs(
                        id = id,
                        selectedImages = selectedImages,
                        observacion = observacion,
                        signatureBitmap = signature.bitmap,
                        context = context,
                        esEncargado = signature.esEncargado,
                        nombreEncargado = signature.nombreEncargado,
                        identificacionEncargado = signature.identificacionEncargado
                    )
                }
            },
            onCancel = {
                signatureBitmap.value = null
                showSignatureDialog.value = false
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            userScrollEnabled = !isLoading && !isUploadingFile
        ) {
            item {
                LazyImages(selectedImages, selectedPreviewUri, context, isCompressing, true)
            }

            item {
                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        TextArea(
                            value = observacion.value,
                            onValueChange = { observacion.value = it },
                            label = "Observacion",
                            placeholder = "",
                            required = true
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // Por el momento no se solicita material al finalizar tarea
            /*
            if(type.contains("finalizar")) {
                item {
                    Spacer(Modifier.height(16.dp))
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
                                text = "Artículos",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            when {
                                isLoadingArticles -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(40.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Cargando artículos...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                articulosData.isNullOrEmpty() -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No hay artículos disponibles",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                else -> {
                                    articulosData?.forEach { articulo ->
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = articulo.nombre,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )

                                            NumberField(
                                                number = articulo.cantidad,
                                                label = "Cantidad",
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = !isLoading,
                                                showButtons = true,
                                                minValue = 0,
                                                maxValue = Int.MAX_VALUE,
                                                required = false,
                                                onChange = { nuevaCantidad ->
                                                    viewModel.updateArticuloCantidad(
                                                        articulo.id,
                                                        nuevaCantidad
                                                    )
                                                }
                                            )

                                            if (articulo != articulosData?.last()) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(vertical = 8.dp),
                                                    color = MaterialTheme.colorScheme.outlineVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            */
            item {
                CustomButton(
                    isLoading,
                    disabled = !isFormValid || isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    title = if (type.contains("finalizar")) "Finalizar" else "Crear",
                    chargeTitle = "Subiendo...",
                    disabledTitle = "Campos vacíos",
                ) {
                    showSignatureDialog.value = true
                }
            }
        }
    }

    BlockingLoadingOverlay(
        visible = isLoading,
        title = if (type.contains("finalizar")) "Finalizando tarea..." else "Enviando observación...",
        subtitle = "Subiendo datos. No cierres la app ni salgas de esta pantalla"
    )

    // Diálogos de mensajes
    successMessage?.let { message ->
        val goBack = {
            viewModel.clearMessages()
            if (type.contains("finalizar")) {
                navigateToHome()
            } else {
                navigateToUp()
            }
        }
        ModernDialog(
            type = DialogType.SUCCESS,
            message = message,
            title = "¡Éxito!",
            onCancel = goBack,
            onSuccess = goBack,
            cancelText = "Cerrar",
            successText = "Continuar"
        )
    }

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

    warningMessage?.let {
        ModernDialog(
            type = DialogType.WARNING,
            message = it,
            title = "Advertencia",
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