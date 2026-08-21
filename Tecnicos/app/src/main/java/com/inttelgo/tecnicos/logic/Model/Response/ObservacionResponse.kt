package com.inttelgo.tecnicos.logic.Model.Response

import com.google.gson.annotations.SerializedName

data class ObservacionResponse(
    val success: Boolean,
    @SerializedName(value = "mensaje", alternate = ["message"])
    val mensaje: String? = null,
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
    @SerializedName(value = "mensaje", alternate = ["message"])
    val mensaje: String? = null,
)
