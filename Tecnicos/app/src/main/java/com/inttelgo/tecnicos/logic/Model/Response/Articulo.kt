package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.Articulo

data class ArticuloInstResponse (
    val success: Boolean = false,
    val message: String = "",
    val articulos: List<Articulo> = emptyList()
)