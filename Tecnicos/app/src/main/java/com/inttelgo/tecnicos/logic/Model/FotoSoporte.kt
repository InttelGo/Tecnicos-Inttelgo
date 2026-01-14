package com.inttelgo.tecnicos.logic.Model

data class FotoSoporte(
    val id_foto: String="",
    val link: String ="",
    val fecha: String = "",
    val ubicacion: String = "",
    val id_obs_ticket: String? = null
)