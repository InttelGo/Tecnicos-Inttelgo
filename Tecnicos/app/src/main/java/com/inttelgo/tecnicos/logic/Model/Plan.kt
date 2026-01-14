package com.inttelgo.tecnicos.logic.Model

data class Plan(
    val id: Int = 0,
    val cantidad: Int = 0,
    val valor: Double=0.0,
    val obs: String ="",
    val valor_real: Double =0.0,
    val tipo_plan: TipoPlan? = null,
    val tipo_servicio: TipoServicio? = null
)