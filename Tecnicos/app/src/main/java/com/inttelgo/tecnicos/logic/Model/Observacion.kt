package com.inttelgo.tecnicos.logic.Model

data class Observacion(
    val descripcion: String = "",
    val id: String = "",
    val fecha: String = "",
    val usuario: Usuario? = null
)