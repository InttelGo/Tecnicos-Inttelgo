package com.inttelgo.tecnicos.logic.Model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    var column: String = "",
    var operator: String = "contains",
    @Contextual var value: Any = "",
    val logic: String? = null
)
