package com.inttelgo.tecnicos.logic.Model

data class Barrio(
    val id: Int = 0,
    val descripcion: String = "",
    val prefijo: String = "",
    val red: Int = 0
)

data class BarriosResponse (
    val success: Boolean,
    val barrios: List<Barrio>? = null,
    val mensaje: String
)