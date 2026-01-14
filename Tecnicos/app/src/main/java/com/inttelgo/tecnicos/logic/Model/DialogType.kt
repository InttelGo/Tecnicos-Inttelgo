package com.inttelgo.tecnicos.logic.Model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class DialogType (
    val icon: ImageVector,
    val iconColor: Color,
    val backgroundColor: Color,
    val buttonColor: Color
) {
    SUCCESS(
        icon = Icons.Default.CheckCircle,
        iconColor = Color(0xFF4CAF50),
        backgroundColor = Color(0xFF4CAF50),
        buttonColor = Color(0xFF4CAF50)
    ),
    ERROR(
        icon = Icons.Default.Info,
        iconColor = Color(0xFFE57373),
        backgroundColor = Color(0xFFE57373),
        buttonColor = Color(0xFFE57373)
    ),
    WARNING(
        icon = Icons.Default.Warning,
        iconColor = Color(0xFFFF9800),
        backgroundColor = Color(0xFFFF9800),
        buttonColor = Color(0xFFFF9800)
    ),
    INFO(
        icon = Icons.Default.Info,
        iconColor = Color(0xFF2196F3),
        backgroundColor = Color(0xFF2196F3),
        buttonColor = Color(0xFF2196F3)
    ),
    QUESTION(
        icon = Icons.Default.Build,
        iconColor = Color(0xFF9C27B0),
        backgroundColor = Color(0xFF9C27B0),
        buttonColor = Color(0xFF9C27B0)
    )
}