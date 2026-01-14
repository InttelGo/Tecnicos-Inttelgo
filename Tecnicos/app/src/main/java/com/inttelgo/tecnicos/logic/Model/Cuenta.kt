package com.inttelgo.tecnicos.logic.Model

data class Cuenta(
    val idCuenta: Int = 0,
    val barrio: Barrio? = null,
    val numero_cuenta: String = "",
    val fechaInstalacion: String = "",
    val mac: String ="",
    val diaCorte: Int =0,
    val direccion: String = "",
    val mesesClausula: String = "",
    val clausulaExt: String ="",
    val fechaCreacion: String = "",
    val plan: Plan? = null,
    val cliente: Cliente? = null,
    val ciudad: Ciudad? = null,
    val estadoCuenta: EstadoCuenta? = null
)
