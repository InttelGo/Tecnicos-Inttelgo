package com.inttelgo.tecnicos.logic.Model

data class EstadoInstalacion(
    val id:Int = 0,
    val descripcion: String ="",
    val fecha: String = "",
    val usuario: Usuario? = null
)
