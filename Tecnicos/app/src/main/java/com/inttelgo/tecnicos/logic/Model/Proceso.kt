package com.inttelgo.tecnicos.logic.Model

data class Proceso(
    val id: Int? = null,
    val identificacion: String? = null,
    val nombre: String? = null,
    val direccion: String? = null,
    val condominio: String? = null,
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
    val medias: List<FotoInsta>? = null
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

data class updateInstallationBody(
    val fecha_r: String? = null,
    val fecha_i: String? = null,
    val fecha_ini: String? = null,
    val estado: Number? = null,
    val usuario_creacion: Number?=null,
    val usuario_inicio: Number?=null,
    val usuario_finalizacion: Number?=null
)