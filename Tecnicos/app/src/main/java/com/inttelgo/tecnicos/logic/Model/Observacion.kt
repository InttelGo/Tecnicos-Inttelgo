package com.inttelgo.tecnicos.logic.Model

import com.google.gson.annotations.SerializedName

data class Observacion(
    @SerializedName(value = "descripcion", alternate = ["observacion", "content"])
    val descripcion: String = "",
    val id: String = "",
    @SerializedName(value = "fecha", alternate = ["create_at", "fecha_creacion"])
    val fecha: String = "",
    @SerializedName(value = "usuario", alternate = ["create_by"])
    val usuario: Usuario? = null
)
