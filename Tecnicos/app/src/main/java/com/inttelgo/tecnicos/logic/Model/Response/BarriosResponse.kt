package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.Barrio

data class BarriosResponse (
    val success: Boolean,
    val barrios: List<Barrio>,
    val mensaje: String
)