package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.Ticket

data class TicketResponse(
    val success: Boolean = false,
    val mensaje: String = "",
    val ticket: Ticket? = null,
)
