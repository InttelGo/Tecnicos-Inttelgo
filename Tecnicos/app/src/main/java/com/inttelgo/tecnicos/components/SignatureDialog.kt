package com.inttelgo.tecnicos.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.gcacace.signaturepad.views.SignaturePad
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.logic.Model.SignatureResult

/**
 * Diálogo compartido de firma (instalación, soporte y tarea).
 * Canvas horizontal 16:9 con guía de dirección; exporta en landscape.
 */
@Composable
fun SignatureDialog(
    onConfirm: (SignatureResult) -> Unit,
    onCancel: () -> Unit
) {
    var signatureBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var signaturePadView by remember { mutableStateOf<SignaturePad?>(null) }
    var isValid by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }
    var nombreEncargado by remember { mutableStateOf("") }
    var identificacionEncargado by remember { mutableStateOf("") }
    val esEncargado = selectedTab == 1
    val encargadoIncompleto = esEncargado &&
            (nombreEncargado.trim().isEmpty() || identificacionEncargado.trim().isEmpty())

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Firma del Comprobante",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SignatureTypeTab(
                            text = "Titular",
                            selected = selectedTab == 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedTab = 0
                            nombreEncargado = ""
                            identificacionEncargado = ""
                        }
                        SignatureTypeTab(
                            text = "Encargado",
                            selected = selectedTab == 1,
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedTab = 1
                        }
                    }
                }

                if (esEncargado) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = nombreEncargado,
                        onValueChange = { nombreEncargado = it },
                        label = "Nombre del encargado",
                        leadingIcon = R.drawable.ic_circle_user_round,
                        placeholder = "Nombre completo",
                        required = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = identificacionEncargado,
                        onValueChange = { identificacionEncargado = it },
                        label = "Identificación del encargado",
                        leadingIcon = R.drawable.ic_file,
                        placeholder = "Número de identificación",
                        required = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Firme en horizontal, de izquierda → a derecha",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AndroidView(
                    factory = { context ->
                        FrameLayout(context).apply {
                            setBackgroundColor(android.graphics.Color.parseColor("#F2F2F2"))

                            addView(
                                SignatureGuideView(context),
                                FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                                )
                            )

                            val pad = SignaturePad(context, null).apply {
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                setOnSignedListener(object : SignaturePad.OnSignedListener {
                                    override fun onStartSigning() {
                                        isValid = false
                                    }

                                    override fun onSigned() {
                                        signatureBitmap = signatureBitmap
                                    }

                                    override fun onClear() {
                                        signatureBitmap = null
                                        isValid = true
                                    }
                                })
                                setMinWidth(2f)
                                setMaxWidth(4f)
                                setVelocityFilterWeight(0.9f)
                            }
                            addView(
                                pad,
                                FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                                )
                            )
                            signaturePadView = pad
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = {
                        signaturePadView?.clear()
                        signatureBitmap = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Limpiar")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    CustomButton(
                        isLoading = false,
                        disabled = isValid || encargadoIncompleto,
                        title = "Confirmar",
                        chargeTitle = "Procesando...",
                        disabledTitle = if (encargadoIncompleto) "Datos requeridos" else "Firma requerida",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            signaturePadView?.let { pad ->
                                if (!pad.isEmpty) {
                                    val bitmap = pad.signatureBitmap.ensureLandscapeSignature()
                                    onConfirm(
                                        SignatureResult(
                                            bitmap = bitmap,
                                            esEncargado = esEncargado,
                                            nombreEncargado = nombreEncargado.trim().takeIf { esEncargado },
                                            identificacionEncargado = identificacionEncargado.trim().takeIf { esEncargado }
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

/** Guía visual detrás del pad: indica firmar en horizontal, de izquierda a derecha. */
private class SignatureGuideView(context: Context) : View(context) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#BDBDBD")
        strokeWidth = 3f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(18f, 12f), 0f)
    }
    private val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#F57C00")
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#757575")
        isFakeBoldText = true
    }
    private val startPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#F57C00")
        isFakeBoldText = true
    }

    override fun onDraw(canvas: AndroidCanvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        val margin = width * 0.06f
        val lineY = height * 0.78f
        val arrowY = height * 0.22f

        startPaint.textSize = height * 0.09f
        canvas.drawText("INICIO", margin, lineY - height * 0.06f, startPaint)

        canvas.drawLine(margin, lineY, width - margin, lineY, linePaint)

        textPaint.textSize = height * 0.075f
        canvas.drawText(
            "Firme aquí →",
            margin,
            arrowY - height * 0.04f,
            textPaint
        )

        val arrowEnd = width - margin
        canvas.drawLine(margin, arrowY, arrowEnd - 12f, arrowY, accentPaint)
        canvas.drawLine(arrowEnd - 36f, arrowY - 18f, arrowEnd - 8f, arrowY, accentPaint)
        canvas.drawLine(arrowEnd - 36f, arrowY + 18f, arrowEnd - 8f, arrowY, accentPaint)
    }
}

/** Asegura que la firma se guarde en formato horizontal (ancho >= alto). */
private fun Bitmap.ensureLandscapeSignature(): Bitmap {
    if (width >= height) return this
    val matrix = Matrix().apply { postRotate(-90f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

@Composable
private fun SignatureTypeTab(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            )
        }
    }
}
