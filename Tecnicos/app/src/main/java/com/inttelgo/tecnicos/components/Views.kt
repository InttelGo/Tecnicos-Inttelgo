package com.inttelgo.tecnicos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
            )
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
