package com.inttelgo.tecnicos.logic.Model

data class Articulo(
    val id: String = "",
    val nombre: String = "",
    var cantidad: Int = 0,
    val fecha_ingreso: String = "",
    val tipo: TipoArticulo? = null
)

data class ArticuloInstResponse (
    val success: Boolean = false,
    val message: String = "",
    val articulos: List<Articulo> = emptyList()
)

data class TipoArticulo (
    val id:Int = 0,
    val descripcion: String = ""
)