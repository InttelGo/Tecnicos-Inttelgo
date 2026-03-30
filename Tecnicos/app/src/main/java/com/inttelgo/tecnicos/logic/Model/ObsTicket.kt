package com.inttelgo.tecnicos.logic.Model

data class ObsTicket(
    val id: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val usuario: Usuario? = null
)

data class ObsTicketResponse(
    val success: Boolean = false,
    val mensaje: String ="",
    val observaciones: List<ObsTicket>? = emptyList(),
    val totalPages: Int = 1
)

data class ObsTicketEvidenciaResponse(
    val success: Boolean = false,
    val mensaje: String ="",
    val evidencias: List<FotoSoporte>? = emptyList()
)
