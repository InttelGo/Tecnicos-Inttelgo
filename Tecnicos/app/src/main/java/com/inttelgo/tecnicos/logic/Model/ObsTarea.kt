package com.inttelgo.tecnicos.logic.Model

data class ObsTarea(
    val fecha: String = "",
    val id: String = "",
    val observacion: String = "",
    val usuario: Usuario? = null
)

data class ObsTareaResponse(
    val success: Boolean = false,
    val mensaje: String ="",
    val observaciones: List<ObsTarea>? = emptyList(),
    val totalPages: Int = 1
)

data class ObsTareaEvidenciaResponse(
    val success: Boolean = false,
    @com.google.gson.annotations.SerializedName(value = "mensaje", alternate = ["message"])
    val mensaje: String = "",
    val observacion: ObservacionConEvidencias? = null,
    val evidencias: List<EvidenciaMedia>? = emptyList()
) {
    fun resolvedEvidencias(): List<EvidenciaMedia> {
        return observacion?.evidencias ?: evidencias ?: emptyList()
    }
}
