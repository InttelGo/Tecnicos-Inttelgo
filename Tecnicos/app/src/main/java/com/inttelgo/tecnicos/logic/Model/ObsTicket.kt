package com.inttelgo.tecnicos.logic.Model

import com.google.gson.annotations.SerializedName

data class ObsTicket(
    val id: String = "",
    @SerializedName(value = "content", alternate = ["observacion"])
    val content: String = "",
    @SerializedName(value = "create_at", alternate = ["fecha", "fecha_creacion"])
    val create_at: String = "",
    @SerializedName(value = "create_by", alternate = ["usuario"])
    val create_by: Usuario? = null
)

data class ObsTicketResponse(
    val success: Boolean = false,
    @SerializedName(value = "mensaje", alternate = ["message"])
    val mensaje: String = "",
    val observaciones: List<ObsTicket>? = emptyList(),
    val totalPages: Int = 1
)

data class ObsTicketEvidenciaResponse(
    val success: Boolean = false,
    @SerializedName(value = "mensaje", alternate = ["message"])
    val mensaje: String = "",
    val observacion: ObservacionConEvidencias? = null,
    val evidencias: List<EvidenciaMedia>? = emptyList()
) {
    fun resolvedEvidencias(): List<EvidenciaMedia> {
        return observacion?.evidencias ?: evidencias ?: emptyList()
    }
}
