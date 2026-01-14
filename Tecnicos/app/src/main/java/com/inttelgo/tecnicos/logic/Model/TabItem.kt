package com.inttelgo.tecnicos.logic.Model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class TabItem (
    val title: String,
    val unselectedColor: Color = Color.Gray,
    val selectedGradient: Brush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFFA726), Color(0xFFFF5722))),
    val screen: @Composable () -> Unit

)