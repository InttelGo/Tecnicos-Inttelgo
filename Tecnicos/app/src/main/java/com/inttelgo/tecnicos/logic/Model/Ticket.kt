package com.inttelgo.tecnicos.logic.Model

data class Ticket(
    val cliente: Cliente,
    val fecha_hora: String,
    val id_ticket: String,
    val observacion_u: String,
    val tipo: Tipo
)