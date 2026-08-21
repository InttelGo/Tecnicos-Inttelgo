package com.inttelgo.tecnicos.logic.Model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    var column: String? = null,
    var operator: String? = null,
    @Contextual var value: Any? = null,
    val logic: String? = null,
    /** Grupo anidado: (filtro1 OR filtro2) AND ... */
    val filters: List<Filter>? = null
)
