package com.inttelgo.tecnicos.logic.Model

data class TicketResult(
    val success: Boolean,
    val tickets: List<Ticket>
)