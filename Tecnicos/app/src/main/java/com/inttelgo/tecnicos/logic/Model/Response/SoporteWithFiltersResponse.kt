package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.Ticket

data class SoporteWithFiltersResponse (
    val success: Boolean,
    val tickets: List<Ticket>,
    val total: Int,
    val mensaje: String
)