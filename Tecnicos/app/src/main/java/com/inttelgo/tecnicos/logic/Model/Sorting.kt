package com.inttelgo.tecnicos.logic.Model

import kotlinx.serialization.Serializable

@Serializable
data class Sorting(
    val id: String = "",
    val desc: Boolean = true
)
