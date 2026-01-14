package com.inttelgo.tecnicos.logic.Model

data class Ticket(
    val id_ticket: String = "",
    val observacion_ticket: String = "",
    val observacion_ticket_tec: String = "",
    val prioridad: Prioridad? =null,
    val fecha_hora: String = "",
    val fecha_hora_tec: String = "",
    val fecha_hora_edit: String = "",
    val estado: EstadoTicket? = null,
    val tipo: Tipo? = null,
    val usuarioCreacion: Usuario? = null,
    val tecnico: Usuario? = null,
    val usuarioModificacion: Usuario? = null,
    val barrio: Barrio? = null,
    val cuenta: Cuenta? = null,
    val cliente: Cliente? = null
)