package com.inttelgo.tecnicos.logic.Model

data class Proceso(
    val id: Int? = null,
    val identificacion: String? = null,
    val nombre: String? = null,
    val direccion: String? = null,
    val telefonos: String? = null,
    val fecha_r: String? = "",
    val fecha_i: String? = "",
    val fecha_ini: String? = "",
    val estado: EstadoInstalacion? = null,
    val plan: Plan? = null,
    val barrio: Barrio? = null,
    val usuario_creacion: Usuario?=null,
    val usuario_inicio: Usuario?=null,
    val usuario_finalizacion: Usuario?=null,
    val observacion: Observacion? = null,
    val tipo_plan: TipoPlan? = null,
    val tipo_servicio: TipoServicio? = null
)

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