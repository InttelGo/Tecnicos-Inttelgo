package com.inttelgo.tecnicos.logic.Model

import com.google.gson.annotations.SerializedName

data class ObsInstalacionResponse(
    val success: Boolean = false,
    @SerializedName(value = "mensaje", alternate = ["message"])
    val mensaje: String = "",
    val observaciones: List<Observacion>? = emptyList(),
    val totalPages: Int = 1,
    val filas: Int = 0
)

data class ObsInstalacionEvidenciaResponse(
    val success: Boolean = false,
    @SerializedName(value = "mensaje", alternate = ["message"])
    val mensaje: String = "",
    val observacion: ObservacionConEvidencias? = null,
    val evidencias: List<EvidenciaMedia>? = null
) {
    fun resolvedEvidencias(): List<EvidenciaMedia> {
        return observacion?.evidencias ?: evidencias ?: emptyList()
    }
}
