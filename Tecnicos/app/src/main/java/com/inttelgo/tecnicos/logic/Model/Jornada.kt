package com.inttelgo.tecnicos.logic.Model

/**
 * Campos de jornada. Gson ignora propiedades extra (usuario, update_by, ingreso_almuerzo, etc.).
 * Visibilidad de botones: solo [ingreso] y [salida].
 */
data class Jornada(
    val id: Int? = null,
    val id_jornada: Int? = null,
    /** ISO datetime, ej. 2026-08-13T05:00:00.000Z */
    val dia: String? = null,
    val ingreso: BiometricCheck? = null,
    val primer_servicio: PrimerServicio? = null,
    val salida: BiometricCheck? = null,
    val retraso: Retraso? = null,
    val ausencia: Boolean = false,
    val observacion_supervision: String? = null
)

/**
 * Marca biométrica + datos del QR escaneado.
 * QR ejemplo:
 * {"id":78,"usuario":"Edward G. Castillo B.","fecha_creacion":"...","oficina":{"id":2,"descripcion":"Santo Domingo"}}
 */
data class BiometricCheck(
    val hora: String? = null,
    val huella: Boolean? = null,
    val id: Int? = null,
    val usuario: String? = null,
    val fecha_creacion: String? = null,
    val oficina: OficinaQr? = null
)

/** JSON: { "id": 123, "tipo": "tarea"|"ticket"|"instalacion", "hora": "..." } */
data class PrimerServicio(
    val id: Int? = null,
    val tipo: String? = null,
    val hora: String? = null
)

/** JSON: { "hora": "..." } */
data class Retraso(
    val hora: String? = null
)

/** Contenido completo del QR de jornada. */
data class JornadaQrPayload(
    val id: Int = 0,
    val usuario: String = "",
    val fecha_creacion: String = "",
    val oficina: OficinaQr = OficinaQr()
)

data class OficinaQr(
    val id: Int = 0,
    val descripcion: String = ""
)

data class JornadaResponse(
    val success: Boolean = false,
    val message: String? = null,
    val jornada: Jornada? = null
)

data class UpdateJornadaRequest(
    val ingreso: BiometricCheck? = null,
    val salida: BiometricCheck? = null,
    val primer_servicio: PrimerServicio? = null
)

enum class JornadaCheckType {
    INGRESO,
    SALIDA
}

object PrimerServicioTipo {
    const val TAREA = "tarea"
    const val TICKET = "ticket"
    const val INSTALACION = "instalacion"
}
