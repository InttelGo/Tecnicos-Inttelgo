package com.inttelgo.tecnicos.layout

import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.inttelgo.tecnicos.components.TextArea
import com.inttelgo.tecnicos.logic.Model.DialogType
import com.inttelgo.tecnicos.viewmodel.ProcesoViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ObsProceso(
    id: String,
    context: Context,
    navigateToUp: () -> Unit,
    modifier: Modifier,
    selectedPreviewUri: MutableState<Uri?>,
    isCompressing: MutableState<Boolean>
) {
    val viewModel: ProcesoViewModel = remember { ProcesoViewModel() }
    val isUploadingFile by viewModel.isUploadingFile.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val warningMessage by viewModel.warningMessage.collectAsState()
    val selectedImages = remember { mutableStateOf<List<Uri?>>(emptyList()) }
    val observacion = remember { mutableStateOf("") }
    val isLoading by viewModel.uploadingLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    // Las evidencias son opcionales: se puede crear una observación sin archivos
    val isFormValid = observacion.value.trim().isNotEmpty()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            userScrollEnabled = !isLoading && !isUploadingFile
        ) {
            item {
                LazyImages(selectedImages, selectedPreviewUri, context, isCompressing, false)
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

            item {
                CustomButton(
                    isLoading,
                    disabled = !isFormValid || isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    title = "Crear",
                    chargeTitle = "Subiendo...",
                    disabledTitle = "Campos vacíos",
                ) {
                    viewModel.createObs(id, selectedImages, observacion, context)
                }
            }
        }
    }

    BlockingLoadingOverlay(
        visible = isLoading,
        title = "Enviando observación...",
        subtitle = "Subiendo datos. No cierres la app ni salgas de esta pantalla"
    )

    successMessage?.let {
        ModernDialog(
            type = DialogType.SUCCESS,
            message = it.message,
            title = "¡Éxito!",
            onCancel = {
                viewModel.clearMessages()
            },
            onSuccess = {
                viewModel.clearMessages()
                navigateToUp()
            },
            cancelText = "Cerrar",
            successText = "Continuar"
        )
    }

    errorMessage?.let {
        ModernDialog(
            type = DialogType.ERROR,
            message = it.message,
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
            message = it.message,
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