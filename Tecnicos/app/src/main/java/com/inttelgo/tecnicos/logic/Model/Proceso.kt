package com.inttelgo.tecnicos.logic.Model

data class Proceso(
    val direccion: String,
    val fecha_r: String,
    val fecha_ini: String? =null,
    val id_estado_instalacion: String,
    val id_instalacion: String ="",
    val nombre: String,
    val observacion: Observacion,
    val telefonos: String
)