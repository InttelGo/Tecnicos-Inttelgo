package com.inttelgo.tecnicos.logic.Model

import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    var column: String = "",
    var operator: String = "contains",
    var value: String = "",
    val logic: String? = null
)
