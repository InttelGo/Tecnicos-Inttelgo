package com.inttelgo.tecnicos.logic.Model

data class Ticket(
    val prioridad: Prioridad? =null,
    val id: String = "",
    val observacion_ticket: String = "",
    val observacion_ticket_tec: String = "",
    val fecha_hora: String = "",
    val fecha_hora_tec: String = "",
    val fecha_hora_edit: String = "",
    val tipo: Tipo? = null,
    val estado: EstadoTicket? = null,
    val barrio: Barrio? = null,
    val usuario_creacion: Usuario? = null,
    val usuario_modificacion: Usuario? = null,
    val tecnico: Usuario? = null,
    val cuenta: Cuenta? = null,
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
