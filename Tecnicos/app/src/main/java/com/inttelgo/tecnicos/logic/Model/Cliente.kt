package com.inttelgo.tecnicos.logic.Model

data class Cliente(
    val nroCliente: String,
    val apellido_1: String,
    val direccion: String,
    val nombre_1: String,
    val telefono_1: String,
    val telefono_2: String? =null,
    val barrio: String
)