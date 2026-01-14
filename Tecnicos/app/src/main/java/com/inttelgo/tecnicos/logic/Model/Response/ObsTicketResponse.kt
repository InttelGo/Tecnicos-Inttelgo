package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.FotoSoporte
import com.inttelgo.tecnicos.logic.Model.ObsTicket

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
