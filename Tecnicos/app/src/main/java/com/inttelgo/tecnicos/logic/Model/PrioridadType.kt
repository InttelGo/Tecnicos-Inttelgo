package com.inttelgo.tecnicos.logic.Model

import androidx.compose.ui.graphics.Color

sealed class PrioridadType (
    val descripcion: String,
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color
)  {
    data object Alta : PrioridadType(
        descripcion = "Alta",
        backgroundColor = Color(0xFFFFEBEE), // Rojo muy claro
        textColor = Color(0xFFC62828),       // Rojo oscuro
        borderColor = Color(0xFFEF5350)      // Rojo medio
    )

    data object Media : PrioridadType(
        descripcion = "Media",
        backgroundColor = Color(0xFFFFF9C4), // Amarillo muy claro
        textColor = Color(0xFFF57F17),       // Amarillo oscuro
        borderColor = Color(0xFFFFEB3B)      // Amarillo medio
    )

    data object Baja : PrioridadType(
        descripcion = "Baja",
        backgroundColor = Color(0xFFE3F2FD), // Azul muy claro
        textColor = Color(0xFF1565C0),       // Azul oscuro
        borderColor = Color(0xFF42A5F5)      // Azul medio
    )

    data object Ninguna : PrioridadType(
        descripcion = "Ninguna",
        backgroundColor = Color(0xFFF5F5F5), // Gris muy claro
        textColor = Color(0xFF757575),       // Gris medio
        borderColor = Color(0xFFBDBDBD)      // Gris claro
    )

    companion object {
        fun fromString(color: String?): PrioridadType {
            return when (color?.lowercase()) {
                "alta" -> Alta
                "media" -> Media
                "baja" -> Baja
                else -> Ninguna
            }
        }
    }
}