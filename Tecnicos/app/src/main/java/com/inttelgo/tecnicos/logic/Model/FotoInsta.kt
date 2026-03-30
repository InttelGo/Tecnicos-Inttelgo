package com.inttelgo.tecnicos.logic.Model

data class FotoInsta(
    val id: String = "",
    val link: String = "",
    val fecha: String = "",
    val ubicacion: String = ""
)

data class DeleteImageResponse(
    val success: Boolean = false,
    val message: String = ""
)