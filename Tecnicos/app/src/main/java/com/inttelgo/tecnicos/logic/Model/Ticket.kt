package com.inttelgo.tecnicos.logic.Model

data class Ticket(
    val prioridad: Prioridad? =null,
    val id: String = "",
    val observation: String = "",
    val user_observation: String = "",
    val create_at: String = "",
    val reserved_at: String = "",
    val end_at: String = "",
    val update_at: String = "",
    val type: Tipo? = null,
    val status: EstadoTicket? = null,
    val barrio: Barrio? = null,
    val create_by: Usuario? = null,
    val update_by: Usuario? = null,
    val operator_by: Usuario? = null,
    val assistant_by: Usuario? = null,
    val service: Cuenta? = null,
    val cliente: Cliente? = null
)

data class SoporteWithFiltersResponse (
    val success: Boolean,
    val tickets: List<Ticket>,
    val totalPages: Int,
    val mensaje: String
)

data class TicketResponse(
    val success: Boolean = false,
    val mensaje: String = "",
    val ticket: Ticket? = null,
)
