package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.FotoSoporte
import com.inttelgo.tecnicos.logic.Model.ObsTarea

data class ObsTareaResponse(
    val success: Boolean = false,
    val mensaje: String ="",
    val observaciones: List<ObsTarea>? = emptyList(),
    val totalPages: Int = 1
)

data class ObsTareaEvidenciaResponse(
    val success: Boolean = false,
    val mensaje: String ="",
    val evidencias: List<FotoSoporte>? = emptyList()
)
