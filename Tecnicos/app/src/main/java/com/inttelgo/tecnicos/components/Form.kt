package com.inttelgo.tecnicos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inttelgo.tecnicos.R

@Composable
fun TextFlieldCustom (type: String, title: String, content: MutableState<String>, w: Dp ){
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
fun ButtonRainbow(text: String, modifier: Modifier, onClick: () -> Unit){
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
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFff9900), // Color morado oscuro
                            Color(0xFFff6700)  // Color lila claro
                        )
                    )
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
                    color = Color(0xFFffffff)
                )
            )
        }
    }
}

@Composable
fun SearchInput(search: MutableState<String>, onClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(28.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = search.value,
            onValueChange = { search.value = it},
            label = { Text(
                "Buscar",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Black,
                )
            ) },
            modifier = Modifier
        )
        Box(
            modifier = Modifier
                .size(57.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFff9900), // Color naranja oscuro
                            Color(0xFFff6700)  // Color naranja claro
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center // Alinea el contenido horizontal y verticalmente
        ) {
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(25.dp) // Tamaño del ícono
                )
            }
        }
    }
}