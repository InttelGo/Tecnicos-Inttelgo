package com.inttelgo.tecnicos.logic.Model

data class ObsTarea(
    val fecha: String = "",
    val id_obs_tarea: String = "",
    val observacion: String = "",
    val usuario: Usuario? = null
)
