package com.inttelgo.tecnicos.logic.Model

data class Proceso(
    val id: Int? = null,
    val identificacion: String? = null,
    val nombre: String? = null,
    val direccion: String? = null,
    val telefonos: String? = null,
    val fecha_r: String = "",
    val fecha_ini: String = "",
    val estado: EstadoInstalacion? = null,
    val barrio: Barrio? = null,
    val plan: Plan? = null,
    val usuario_creacion: Usuario?=null,
    val usuario_inicio: Usuario?=null,
    val usuario_finalizacion: Usuario?=null,
    val observacion: Observacion? = null,
)