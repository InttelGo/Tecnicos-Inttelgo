package com.inttelgo.tecnicos.components

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inttelgo.tecnicos.R
import com.inttelgo.tecnicos.logic.Model.PriorityData
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
fun PriorityCard(fecha_hora: String){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val inputDate = LocalDateTime.parse(fecha_hora, formatter)

    // Calcular la prioridad con base en la diferencia de horas
    val priorityData = when (calculateHourDifference(inputDate)) {
        in 0 until 8 -> PriorityData(1, "Prioridad Baja","#d1e7dd", "#105132", "#bbdbcc") // Verde
        in 8 until 16 -> PriorityData(2, "Prioridad Media","#fff4cd","#7a641d", "#ffecb5")// Amarillo
        else -> PriorityData(3, "Prioridad Alta", "#f8d7da", "#841f29", "#f5c2c7")// Rojo
    }

    // Mostrar en la interfaz
    Box(
        modifier = Modifier
            .background(
                Color(parseColor(priorityData.backgroundColor)),
                RoundedCornerShape(5.dp)
            )
            .border(
                width = 0.dp,
                color = Color(parseColor(priorityData.borderColor)),
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priorityData.priorityText,
            color = Color(parseColor(priorityData.textColor)),
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
private fun calculateHourDifference(inputDate: LocalDateTime): Long {
    val currentDate = LocalDateTime.now()

    // Manejar diferencias de zona horaria si es necesario
    val duration = if (inputDate.isAfter(currentDate)) {
        Duration.between(currentDate, inputDate)
    } else {
        Duration.between(inputDate, currentDate)
    }
    Log.d("hour_diference", duration.toHours().toString())
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

@Composable
fun WarningCard(message: String){
    Box(
        modifier = Modifier
            .then(
                Modifier.background(
                    color = Color(0xFFfff4cd),
                    shape = RoundedCornerShape(10.dp)
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFFffecb5),
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
                color = Color(0xFF7a641d),
            ),
            modifier = Modifier
                .padding(20.dp)
        )
    }
}

@Composable
fun AnimatedIcon() {
    val scale = remember { Animatable(1f) } // Inicializa la animación con un valor inicial

    LaunchedEffect(Unit) {
        while (true) { // Bucle infinito para alternar subida y bajada
            scale.animateTo(
                targetValue = 1.3f, // Escala hacia arriba
                animationSpec = tween(durationMillis = 1000)
            )
            scale.animateTo(
                targetValue = 1f, // Escala hacia abajo
                animationSpec = tween(durationMillis = 1000)
            )
            delay(200)
        }
    }

    Image(
        painter = painterResource(id = R.drawable.logo_mano),
        contentDescription = "logo",
        modifier = Modifier
            .size(60.dp)
            .scale(scale.value) // Vincula el valor animado
    )
}

@Composable
fun PrioritiesCard(prioritySelected: MutableState<Int>) {
    Row (
        Modifier.fillMaxWidth()
            .padding(start = 20.dp)
    ){
        val listPriorities: List<PriorityData> = listOf(
            PriorityData(0,"Todos","#d0e0f5", "#104493", "#b8d2ef"),
            PriorityData(1,"Baja","#d1e7dd", "#105132", "#bbdbcc"),
            PriorityData(2,"Media","#fff4cd","#7a641d", "#ffecb5"),
            PriorityData(3,"Alta", "#f8d7da", "#841f29", "#f5c2c7")
        )
        listPriorities.forEach { p->
            Box(
                modifier = Modifier
                    .background(
                        if(prioritySelected.value == p.id) Color(parseColor(p.textColor)) else Color(parseColor(p.backgroundColor)) ,
                        RoundedCornerShape(5.dp)
                    )
                    .border(
                        width = 0.dp,
                        color = Color(parseColor(p.borderColor)),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable{
                        prioritySelected.value = p.id
                    }
            ) {
                Text(
                    text = p.priorityText,
                    color = if(prioritySelected.value == p.id) Color(parseColor(p.backgroundColor)) else Color(parseColor(p.textColor)),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(5.dp)
                        .background(Color.Transparent)
                )
            }
            Spacer(Modifier.width(10.dp))
        }
    }
}

@Composable
fun InternetAccess(flag: Boolean){
    Box (
        Modifier.fillMaxWidth()
            .padding(10.dp)
            .background(if(!flag)Color(0xFF424242) else Color(0xFF2aa641)),
        contentAlignment = Alignment.Center
    ){
        Text(
            if(!flag) "Sin conexion a internet" else "De nuevo con conexion",
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(10.dp)
        )
    }
}