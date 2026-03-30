package com.inttelgo.tecnicos.logic.Model

data class Cliente(
    val id: String = "",
    val identificacion: String = "",
    val nombre1: String ="",
    val nombre2: String ="",
    val apellido2: String = "",
    val apellido1: String = "",
    val telefono1: String,
    val telefono2: String? =null,
    val correo: String = "",
    val fecha_creacion: String = "",
)