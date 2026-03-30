package com.inttelgo.tecnicos.logic.Model

data class Cuenta(
    val id: Int = 0,
    val fecha_instalacion: String = "",
    val mac: String ="",
    val dia_corte: Int =0,
    val direccion: String = "",
    val complemento: String = "",
    val nro_cuenta: String = "",
    val valor_internet: Double = 0.0,
    val valor_telefonia: Double =0.0,
    val valor_television: Double = 0.0,
    val valor_total: Double = 0.0,
    val fecha_reconexion: String = "",
    val fecha_creacion: String = "",
    val plan: Any? = null,
    val barrio: Barrio? = null,
    val ciudad: Ciudad? = null,
    val estado: EstadoCuenta? = null,
    val tipo_servicio: TipoServicio? = null,
    val tipo_plan: TipoPlan? = null,
    val cliente: Cliente? = null
)
