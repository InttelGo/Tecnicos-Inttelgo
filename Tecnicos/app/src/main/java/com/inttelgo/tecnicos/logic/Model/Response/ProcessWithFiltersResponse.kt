package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.FotoInsta
import com.inttelgo.tecnicos.logic.Model.Proceso

data class ProcessWithFiltersResponse (
    val procesos: List<Proceso>,
    val total: Int
)

data class EvidenciasInstalationResponse(
    val success: Boolean = false,
    val message: String = "",
    val evidencias: List<FotoInsta>? = null
)

data class CreateEvidenciaInstalationResponse(
    val success: Boolean = false,
    val message: String = "",
    val evidencia: FotoInsta? = null
)

data class FinishInstalationResponse(
    val success: Boolean = false,
    val message: String = ""
)