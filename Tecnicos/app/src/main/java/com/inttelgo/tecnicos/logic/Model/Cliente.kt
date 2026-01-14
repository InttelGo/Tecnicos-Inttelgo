package com.inttelgo.tecnicos.logic.Model

data class Cliente(
    val id: String = "",
    val nombre: String ="",
    val nombre_1: String ="",
    val nombre_2: String ="",
    val apellido_2: String = "",
    val apellido_1: String = "",
    val telefono_1: String,
    val telefono_2: String? =null,
    val correo: String = "",
    val fecha_creacion: String = ""
)