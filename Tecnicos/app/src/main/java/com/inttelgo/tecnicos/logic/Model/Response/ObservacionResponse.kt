package com.inttelgo.tecnicos.logic.Model.Response

data class ObservacionResponse(
    val success: Boolean,
    val mensaje: String?,
    val evidencias: List<Evidencia>? = null,
    val errores: List<String>? = null
)

data class Evidencia(
    val id: String,
    val url: String,
    val tipo: String
)


data class FinishObservacionResponse(
    val success: Boolean = false,
    val mensaje: String? = null,
)