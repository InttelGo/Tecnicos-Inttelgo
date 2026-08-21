package com.inttelgo.tecnicos.logic.Model

import android.graphics.Bitmap

data class Proceso(
    val id: Int? = null,
    val identificacion: String? = null,
    val nombre: String? = null,
    val direccion: String? = null,
    val condominio: String? = null,
    val telefonos: String? = null,
    val correo: String? = null,
    val create_at: String? = "",
    val update_at: String? = "",
    val files_at: String? = "",
    val init_at: String? = "",
    val installation_at: String? = "",
    val reserved_at: String? = "",
    val end_at: String? = "",
    val firma: String? = "",
    val serial: String? = "",
    val mac: String? = "",
    val nombre_encargado: String? = "",
    val identificacion_encargado: String? = "",
    val es_encargado: Int? = 0,
    val estado: EstadoInstalacion? = null,
    val barrio: Barrio? = null,
    val tipo_servicio: TipoServicio? = null,
    val tipo_plan: TipoPlan? = null,
    val plan: Plan? = null,
    val operator_by: Usuario? = null,
    val assistant_by: Usuario? = null,
    val sale_by: Usuario? = null,
    val adviser_by: Usuario? = null,
    val create_by: Usuario? = null,
    val update_by: Usuario? = null,
    val observacion: Observacion? = null,
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

data class ProcesoDetailResponse(
    val success: Boolean = false,
    val mensaje: String = "",
    val instalacion: Proceso? = null
)

data class updateInstallationBody(
    val init_at: String? = null,
    val estado: Number? = null,
)

data class SignatureResult(
    val bitmap: Bitmap,
    val esEncargado: Boolean,
    val nombreEncargado: String?,
    val identificacionEncargado: String?
)
