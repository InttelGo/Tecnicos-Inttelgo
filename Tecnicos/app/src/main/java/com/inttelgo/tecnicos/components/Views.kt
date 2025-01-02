package com.inttelgo.tecnicos.components

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.inttelgo.tecnicos.R
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun TargetCustom(content: String, isPrimary: Boolean,onClick: () -> Unit) {
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
                width = if (isPrimary) 0.dp else 1.dp,
                color = if (isPrimary) Color.Transparent else Color.Black,
                shape = RoundedCornerShape(50.dp)
            ),
    ) {
        Text(
            text = content,
            color = if(isPrimary)Color.White else Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun PriorityCard(){
    val inputDate = LocalDateTime.of(2024, 12, 31, 18, 0)

    // Calcular la prioridad con base en la diferencia de horas
    val (priorityText, colorHex) = when (calculateHourDifference(inputDate)) {
        in 0 until 8 -> "Prioridad Baja" to "#00FF00" // Verde
        in 8 until 16 -> "Prioridad Media" to "#FFFF00" // Amarillo
        else -> "Prioridad Alta" to "#FF0000" // Rojo
    }

    // Mostrar en la interfaz
    Box(
        modifier = Modifier
            .background(
                Color(parseColor(colorHex)),
                RoundedCornerShape(50.dp)
            )
            .border(
                width = 0.dp,
                color = Color.Transparent,
                shape = RoundedCornerShape(50.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priorityText,
            color = Color.White,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(5.dp)
        )
    }
}

@SuppressLint("NewApi")
private  fun calculateHourDifference(inputDate: LocalDateTime): Long {
    val currentDate = LocalDateTime.now()
    val duration = Duration.between(currentDate, inputDate)
    return duration.toHours()
}

@Composable
fun ButtonWithText(text: String, idIcon: Int, s: Dp, onClick: () -> Unit){
    Column (
        modifier = Modifier
           .width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(s*2)
                //.background(Color.Black)
        ) {
            Icon(
                painter = painterResource(idIcon),
                contentDescription = "Button",
                modifier = Modifier.size(s),
                tint = Color.Black
            )
        }
        Text(text = text)
    }
}

@Composable
fun ImagePreview(imageUri: MutableState<Uri?>){
    AlertDialog(
        onDismissRequest = {
            imageUri.value = null
        },
        title = { Text("Imagen seleccionada") },
        text = {
            Box(
                modifier = Modifier
                   .width(350.dp)
                   .clip(RoundedCornerShape(10.dp))
                   .background(Color.White)
            ) {
                AsyncImage(
                    model = imageUri.value,
                    contentDescription = "Imagen",
                    modifier = Modifier
                       .fillMaxSize()
                       .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
            }
        },
        confirmButton = {
            ButtonRainbow("Aceptar", Modifier.width(50.dp)) {
                imageUri.value = null
            }
        }
    )
}

@Composable
fun FloatingButtons(id: String, navigateToUploadImage: (id: String, type: String) -> Unit){
    val isMenuExpanded = remember{ mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
    ) {
        // Botones secundarios (visibles solo cuando el menú está expandido
        if (isMenuExpanded.value) {
            //Finish Support
            GradientFloatingActionButton(
                gradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                ),
                id= R.drawable.check_small_icon
            ){
                navigateToUploadImage(id, "Finalizar")
            }
            //Take a picture
            GradientFloatingActionButton(
                gradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
                ),
                id= R.drawable.post_add_icon
            ){
                navigateToUploadImage(id, "Soporte")
            }
        }
        GradientFloatingActionButton(
            gradient = Brush.horizontalGradient(
                colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))
            ),
            id= if (isMenuExpanded.value) R.drawable.arrow_drop_down_icon else R.drawable.arrow_drop_upward_icon
        ){
            isMenuExpanded.value = !isMenuExpanded.value
        }
    }
}

@Composable
fun GradientFloatingActionButton(
    gradient: Brush,
    id: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp) // Tamaño estándar de FAB
            .background(gradient, shape = CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun AlertCard(message: String){
    Box(
        modifier = Modifier
            .then(
                Modifier.background(
                    color = Color(0xFFf8d7da),
                    shape = RoundedCornerShape(10.dp)
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFFf5c2c7),
                shape = RoundedCornerShape(10.dp)
            ).width(300.dp)
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF842029),
            ),
            modifier = Modifier
                .padding(20.dp)
        )
    }
}