package com.inttelgo.tecnicos.logic.Model

data class Plan(
    val id: Int = 0,
    val valor_base: Double = 0.0,
    val valor_tv: Double = 0.0,
    val valor_triple_play: Double = 0.0,
    val valor_telefonia: Double = 0.0,
    val delete_at: String = "",
    val tipo_plan: TipoPlan? = null
)