package com.inttelgo.tecnicos.logic.Model

data class Usuario(
    val id: Int = 0,
    val identificacion: String ="",
    val nombre_1: String = "",
    val nombre_2: String = "",
    val apellido_1: String = "",
    val apellido_2: String ="" ,
    val telefono_1: String = "",
    val telefono_2: String = "",
    val direccion: String = "",
    val correoPersonal: String = "",
    val correoCorporativo: String = "",
    val perfil: Perfil? = null,
)
